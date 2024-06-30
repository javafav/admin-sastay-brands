package com.shopme.common.entity;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.Constants;



@Entity
@Table(name = "categories")
public class Category extends IdBasedEntity  {


	@Column(nullable = false, length = 128, unique = true)
	private String name;

	@Column(nullable = false, length = 64, unique = true)
	private String alias;

	private boolean enabled;

	@Column(nullable = false, length = 128)
	private String image;

	@Column(name= "all_parent_ids", length = 256, nullable = true )
	private String allPaentIds;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent")
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();

	
	
	
	
	public Category() {}
	
	
	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default-category.png";
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}

	
	
	
	public Category(Integer id, String name, String alias) {
		
		this.id = id;
		this.name = name;
		this.alias = alias;
	}


	public static Category copyIdAndName(Category category) {
		Category copyCategory = new Category();
		copyCategory.setName(category.getName());
		copyCategory.setId(category.getId());
		return copyCategory;
	}
	
	public static Category copyIdAndName(Category category,String name) {
		Category copyCategory = new Category();
		copyCategory.setName(name);
		copyCategory.setId(category.getId());
		return copyCategory;
		
	}
	
	public static Category copyFull(Category category) {
		Category copyCategory = new Category();
		copyCategory.setName(category.getName());
		copyCategory.setId(category.getId());
		copyCategory.setAlias(category.getAlias());
		copyCategory.setImage(category.getImage());
		copyCategory.setEnabled(category.isEnabled());
		copyCategory.setHasChildren(category.getChildren().size() > 0);
		
		return copyCategory;
		
	}
	
	public static Category copyFull(Category category,String name) {
		Category copyCategory = Category.copyFull(category);
		copyCategory.setName(name);
		return copyCategory;
	}
	
	public Category(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	public boolean isHasChildren() {
		return hasChildren;
	}


	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}


	public String getAllPaentIds() {
		return allPaentIds;
	}


	public void setAllPaentIds(String allPaentIds) {
		this.allPaentIds = allPaentIds;
	}


	@Transient
	public String getImagePath() {
		if(id == null) return "/images/image-thumbnail.png";
		return Constants.S3_BASE_URI + "/category-images/" + this.id + "/" + this.image;
	}
	
	
	
	@Transient
	private boolean hasChildren;






	
	
}
