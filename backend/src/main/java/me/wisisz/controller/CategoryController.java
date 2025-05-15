package me.wisisz.controller;

import me.wisisz.model.Category;
import me.wisisz.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * GET /api/categories
     * 
     * Description:
     * Retrieves a list of all categories.
     * 
     * Response:
     * HTTP 200 OK
     * [{ "id": 1, "categoryName": "Groceries" },
     *  { "id": 2, "categoryName": "Restaurant" },
     *  { "id": 3, "categoryName": "Rent" },
     *  { "id": 4, "categoryName": "Utilities" },
     *  { "id": 5, "categoryName": "Entertainment" }]
     * 
     * Errors:
     * 500 Internal Server Error – In case of server-side issues.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * GET /api/categories/{categoryId}
     * 
     * Description:
     * Retrieves a single category by its ID.
     * 
     * Path Variable:
     * categoryId – Integer – The unique ID of the category.
     * 
     * Response:
     * HTTP 200 OK
     * { "id": 2, "categoryName": "Restaurant" }
     * 
     * HTTP 404 Not Found – If no category is found for the given ID.
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer categoryId) {
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        if (category.isPresent()) {
            return new ResponseEntity<>(category.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
