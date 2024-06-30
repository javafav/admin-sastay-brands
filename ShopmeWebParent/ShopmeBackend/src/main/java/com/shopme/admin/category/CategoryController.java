package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;


@Controller
public class CategoryController {
	
	private String defaultRedirectURL = "redirect:/categories/page/1?sortField=name&sortDir=asc&keyword=";
	
	@Autowired
	private CategoryService service;

	
	@GetMapping("/categories")
	public String listFirstPage() {

		return defaultRedirectURL;

	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") Integer pageNum,@RequestParam(name = "sortDir", required = false) String sortDir,
			@RequestParam(name = "keyword") String keyword, Model model) {
		
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		List<Category> listCategories = service.listByPage(pageInfo, sortDir, pageNum,keyword);
       
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE  - 1;
		
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
	
		

		
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageInfo.getTotalPages());


	    model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());

		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/categories");
		
		
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listCategories", listCategories);

		return "categories/categories";
		
		
	}
	
	

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		List<Category> listCategories = service.categoryListUsedInForm();

		try {
			Category category = service.getCategory(id);
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category With (ID " + id + ")");
			model.addAttribute("listCategories", listCategories);
			return "categories/category_form";

		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/categories/new")
	public String createNewCategory(Model model) {
		List<Category> listCategories = service.categoryListUsedInForm();

		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);

		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes, Model model) throws IOException {

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);
		Category savedCategory = service.save(category);
		String uploadDir = "category-images/" + savedCategory.getId();

		AmazonS3Util.removeFolder(uploadDir);
		AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");

		return defaultRedirectURL;
	}

	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnableStatus(@PathVariable("id") Integer categoryId, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		
		service.updateEnableStatus(categoryId, status);
		
		String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message",
				"The category wih (ID " + categoryId + ") " + messageEnabledOrDisabled + " successfuly!");
		
		

		return defaultRedirectURL;
	}

	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id,RedirectAttributes redirectAttributes) {
		try {
			service.deleteCategory(id);
			String categoryDir = "/category-images" + id;
			AmazonS3Util.removeFolder(categoryDir);
		
			redirectAttributes.addFlashAttribute("message", "The category wih (ID:" + id + ")  deleted successfuly!");
			
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			
		}
		return defaultRedirectURL;
	}
	
	
	@GetMapping("/categories/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		
		List<Category> listCategories = service.categoryListUsedInForm();
		CategoryCsvExporter exporter = new  CategoryCsvExporter();
		exporter.export(listCategories, response);
	}
	
	
	
}
