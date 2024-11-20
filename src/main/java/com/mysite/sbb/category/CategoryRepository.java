package com.mysite.sbb.category;


import com.mysite.sbb.api.categoryApi.CategoryApiDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category,Integer> {
    Category findByName(String Name);
    List<Category> findAll();

    @Query("SELECT new com.mysite.sbb.api.categoryApi.CategoryApiDTO(c.id, c.name) FROM Category c")
    List<CategoryApiDTO> findCategoryIdAndName();
}
