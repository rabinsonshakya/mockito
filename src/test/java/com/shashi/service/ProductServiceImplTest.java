package com.shashi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.shashi.dao.ProductDAO;
import com.shashi.dao.ProductDetailDAO;
import com.shashi.entity.ProductEntity;
import com.shashi.service.exception.ProductException;
import com.shashi.test.ProductData;
import com.shashi.vo.ProductVO;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {
	@Mock
	private ProductDAO dao;

	@Mock
	private ProductDetailDAO detailDao;

	@Spy
	private ProductServiceImpl impl;

	private ProductVO input;
	private ProductEntity entity;

	@Before
	public void setUp() {
		input = ProductData.createProduct();
		entity = ProductData.createProductEntity();
		when(impl.getDao()).thenReturn(dao);
		when(impl.getDetailDao()).thenReturn(detailDao);
		// impl.setDao(dao);
		// impl.setDetailDao(detailDao);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveProductNullProduct() {
		impl.saveProduct(null);
		// fail("Not yet implemented");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveProductNullProductName() {
		final ProductVO input = ProductData.createProduct();
		input.setProductName(null);
		impl.saveProduct(null);
		// fail("Not yet implemented");
	}

	@Test
	public void testCreateProduct() throws SQLException {

		when(dao.createProduct(any(ProductEntity.class))).thenReturn(entity);

		final ProductVO result = impl.saveProduct(input);
		assertResult(result);
		InOrder inorder = inOrder(dao, detailDao);
		inorder.verify(dao, times(1)).createProduct(any(ProductEntity.class));
		inorder.verify(detailDao).saveProductDetail(any(ProductEntity.class));
		verify(dao, never()).find(any(Long.class));
		verify(dao, never()).updateProduct(any(ProductEntity.class));

	}

	private void assertResult(final ProductVO result) {
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(input.getProductName(), result.getProductName());
		assertEquals(input.getDescription(), result.getDescription());
	}

	@Test
	public void testUpdateProduct() throws SQLException {

		input.setId(new Long(2000));
		input.setProductName("Abc product");

		when(dao.find(any(Long.class))).thenReturn(entity);

		final ProductVO result = impl.saveProduct(input);
		assertResult(result);
		InOrder inorder = inOrder(dao, detailDao);
		inorder.verify(dao, times(1)).find(any(Long.class));
		inorder.verify(dao, times(1)).updateProduct(any(ProductEntity.class));
		inorder.verify(detailDao).saveProductDetail(any(ProductEntity.class));

		verify(dao, never()).createProduct(any(ProductEntity.class));

	}

	@Test(expected = ProductException.class)
	public void testUpdateProductException() throws SQLException {

		input.setId(new Long(2000));
		input.setProductName("Abc product");

		when(dao.find(any(Long.class))).thenThrow(new SQLException("The database is not available"));

		impl.saveProduct(input);

	}
}
