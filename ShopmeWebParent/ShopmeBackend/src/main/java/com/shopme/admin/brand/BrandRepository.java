package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchAndPagingRepository;
import com.shopme.common.entity.Brand;

public interface BrandRepository extends SearchAndPagingRepository<Brand, Integer> {

	@Query("SELECT COUNT(b) FROM Brand b WHERE b.id = ?1")
	public Long count(Integer id);
	
	public Brand findByName(String name);


	@Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER By b.name ASC")
	public List<Brand> findAll();

	
	@Query("SELECT b FROM Brand b WHERE b.name LIKE %?1% OR b.id LIKE  %?1% ")
	public Page<Brand> findAll(String keyword, Pageable pageable);
}
