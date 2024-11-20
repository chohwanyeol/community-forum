package com.mysite.sbb.aspect;

import com.mysite.sbb.device.DeviceService;
import com.mysite.sbb.geolocation.GeoLocationService;
import com.mysite.sbb.token.JwtTokenProvider;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import groovy.transform.Final;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.type.TrueFalseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.security.Principal;
import java.util.List;
import java.util.ArrayList;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    @Autowired
    private final DeviceService deviceService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    private final GeoLocationService geoLocationService;


    @Around("execution(* com.mysite.sbb.api.categoryApi.CategoryApiController..*(..)) || " +
            "execution(* com.mysite.sbb.api.questionApi.QuestionApiController..*(..))")

    public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {

        // HTTP 요청에서 Authorization 헤더 추출
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader("Authorization");



        if (authorization == null || !authorization.startsWith("Bearer ")
                || !jwtTokenProvider.validateToken(authorization.substring(7))){
            return ResponseEntity.status(401).body("인증 토큰이 없거나 유효하지 않음");
        }

        // 토큰에서 사용자 이름 추출
        String username = jwtTokenProvider.getUsernameFromToken(authorization.substring(7));




        // 사용자 정보 가져오기
        SiteUser siteUser = userService.getUser(username);


        if (siteUser == null) {
            return ResponseEntity.status(401).body("사용자를 찾을 수 없습니다.");
        }

        // 사용자 정보를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(siteUser, null, new ArrayList<>())
        );


        return joinPoint.proceed();
    }


    @Around("execution(@org.springframework.web.bind.annotation.GetMapping * com.mysite.sbb.MainController.branch(..))")
    public Object UserDeviceCheck(ProceedingJoinPoint joinPoint) throws Throwable {

        // HTTP 요청에서 Authorization 헤더 추출
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = attributes.getRequest();
        String deviceInfo = getDeviceInfo(request);


        if (authentication != null && authentication.isAuthenticated()) {
            // Principal (일반적으로 인증된 사용자는 User 객체로 반환됨)
            Object principal = authentication.getPrincipal();


            if (principal instanceof User) {
                String username = ((User) principal).getUsername();

                // 사용자 정보 가져오기
                SiteUser siteUser = userService.getUser(username);

                // 사용자 정보를 SecurityContext에 설정
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(siteUser, null, new ArrayList<>())
                );

                String ipInfo = getClientIp(request);
                //String locationInfo = getLocationByIp(ipInfo);

                deviceService.CheckDevice(siteUser,deviceInfo,ipInfo/*locationInfo*/);

                return joinPoint.proceed();

            }
        }

        return joinPoint.proceed();

    }




    private String getClientIp(HttpServletRequest request) {
        String clientIp;

        // 클라이언트 IP 추출
        if (request.getHeader("X-Forwarded-For") == null) {
            clientIp = request.getRemoteAddr();
        } else {
            clientIp = request.getHeader("X-Forwarded-For").split(",")[0];
        }

        // IPv6 로컬 주소 처리
        if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
            clientIp = "127.0.0.1"; // IPv4 로컬호스트로 변환
        }

        // IPv6을 IPv4로 변환 (필요 시)
        if (clientIp.contains(":")) { // IPv6 형식 확인
            clientIp = convertIPv6ToIPv4(clientIp);
        }

        // IPv4 유효성 검증
        if (!isValidIPv4(clientIp)) {
            throw new IllegalArgumentException("Invalid IP format: " + clientIp);
        }

        return clientIp;
    }

    private String convertIPv6ToIPv4(String ipv6Address) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipv6Address);
            if (inetAddress instanceof Inet4Address) {
                return inetAddress.getHostAddress(); // IPv4로 변환 성공
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipv6Address; // 변환 실패 시 원래 주소 반환
    }

    private boolean isValidIPv4(String ipAddress) {
        String ipv4Regex =
                "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        return ipAddress.matches(ipv4Regex);
    }



    private String getLocationByIp(String ip) {
        String result = geoLocationService.getGeoLocation(ip);
        return result;
    }



    //user 정보
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        String deviceInfo = extractDeviceInfo(userAgent);
        return deviceInfo;
    }

    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        boolean unKnownDevice = false;
        boolean unKnownEngine = false;
        boolean unKnownOs = false;
        // 기기 종류 (예: iPhone, Android, Windows NT)
        String device = "Unknown Device";
        if (userAgent.contains("iPhone")) {
            device = "iPhone";
        } else if (userAgent.contains("Android")) {
            device = "Android";
        } else if (userAgent.contains("Windows NT")) {
            device = "Windows PC";
        } else if (userAgent.contains("Macintosh")) {
            device = "Mac";
        }
        else{
            unKnownDevice = true;
        }


        // 브라우저 엔진 (예: AppleWebKit/537.36, Gecko/20100101)
        String engine = "Unknown Engine";
        if (userAgent.contains("AppleWebKit")) {
            engine = "AppleWebKit";
        } else if (userAgent.contains("Gecko")) {
            engine = "Gecko";
        }
        else {
            unKnownEngine= true;
        }

        // 운영 체제 주요 버전 (예: Windows NT 10.0, Mac OS X 10_15_7, Linux)
        String osVersion = "Unknown OS";
        if (userAgent.contains("Windows NT")) {
            osVersion = "Windows NT";
        } else if (userAgent.contains("Macintosh")) {
            osVersion = "Mac OS X";
        } else if (userAgent.contains("Linux")) {
            osVersion = "Linux";
        } else{
            unKnownOs=true;
        }
        if (unKnownDevice & unKnownEngine & unKnownOs){
            return "UnKnown";
        }

        return "Device: " + device + ", Engine: " + engine + ", OS: " + osVersion;
    }


}
