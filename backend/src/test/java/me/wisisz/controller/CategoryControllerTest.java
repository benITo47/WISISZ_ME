package me.wisisz.controller;

import me.wisisz.model.Category;
import me.wisisz.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category testCategory;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        // Set up test category
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setCategoryName("Test Category");

        // Set up category list
        categoryList = new ArrayList<>();
        categoryList.add(testCategory);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        when(categoryService.getAllCategories()).thenReturn(categoryList);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testCategory, response.getBody().get(0));

        verify(categoryService).getAllCategories();
    }

    @Test
    void getCategoryById_ShouldReturnCategoryWhenExists() {
        when(categoryService.getCategoryById(anyInt())).thenReturn(Optional.of(testCategory));

        ResponseEntity<Category> response = categoryController.getCategoryById(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCategory, response.getBody());

        verify(categoryService).getCategoryById(1);
    }

    @Test
    void getCategoryById_ShouldReturnNotFoundWhenNotExists() {
        when(categoryService.getCategoryById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(999);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(categoryService).getCategoryById(999);
    }
}