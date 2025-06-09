package me.wisisz.service;

import me.wisisz.model.Category;
import me.wisisz.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

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
        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));

        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_ShouldReturnCategoryWhenExists() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryById(1);

        assertTrue(result.isPresent());
        assertEquals(testCategory, result.get());

        verify(categoryRepository).findById(1);
    }

    @Test
    void getCategoryById_ShouldReturnEmptyWhenNotExists() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(999);

        assertFalse(result.isPresent());

        verify(categoryRepository).findById(999);
    }
}