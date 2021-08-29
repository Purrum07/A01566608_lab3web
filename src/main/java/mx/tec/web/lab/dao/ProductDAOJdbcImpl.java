/*
 * ProductDAOJdbcImpl
 * Version 1.0
 * August 21, 2021 
 * Copyright 2021 Tecnologico de Monterrey
 */
package mx.tec.web.lab.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import mx.tec.web.lab.service.CommentsService;
import mx.tec.web.lab.vo.ProductVO;
import mx.tec.web.lab.vo.SkuVO;

/**
 * @author Enrique Sanchez
 *
 */
@Component("jdbc")
public class ProductDAOJdbcImpl implements ProductDAO {
	/** Id field **/
	public static final String ID = "id";
	
	/** Name field **/
	public static final String NAME = "name";
	
	/** Description field **/
	public static final String DESCRIPTION = "description";
	
	/** Color field**/
	public static final String COLOR = "color";
	
	/** Size field**/
	public static final String SIZE = "size";
	
	/** List Price field**/
	public static final String LIST_PRICE = "listPrice";
	
	/** Sale Price field**/
	public static final String SALE_PRICE = "salePrice";
	
	/** Quantity On Hand field**/
	public static final String QUANTITY_ON_HAND= "quantityOnHand";
	
	/** Small Image Url field**/
	public static final String SMALL_IMAGE_URL= "smallImageUrl";
	
	/** Medium Image Url field**/
	public static final String MEDIUM_IMAGE_URL= "mediumImageUrl";
	
	/** Large Image Url field**/
	public static final String LARGE_IMAGE_URL= "largeImageUrl";

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommentsService commentService;
	
	@Override
	public List<ProductVO> findAll() {
		String sql = "SELECT id, name, description FROM product";

		return jdbcTemplate.query(sql, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();
			List<SkuVO> skusList = new ArrayList<>();

			while(rs.next()){
				String parentId = String.valueOf(rs.getLong(ID));
				String skusSql = "SELECT id, color, size, listprice, saleprice, quantityOnHand, smallImageUrl, mediumImageUrl, largeImageUrl FROM sku WHERE parentProduct_id = "+ parentId;
				
				jdbcTemplate.query(skusSql, (ResultSet skuRs) -> {
					
					while(skuRs.next()) {
						SkuVO skus = new SkuVO(
							skuRs.getLong(ID),
							skuRs.getString(COLOR),
							skuRs.getString(SIZE),
							skuRs.getDouble(LIST_PRICE),
							skuRs.getDouble(SALE_PRICE),
							skuRs.getLong(QUANTITY_ON_HAND),
							skuRs.getString(SMALL_IMAGE_URL),
							skuRs.getString(MEDIUM_IMAGE_URL),
							skuRs.getString(LARGE_IMAGE_URL)
						);
						skusList.add(skus);
					}
				});
				
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					skusList,
					commentService.getComments()
				);

				list.add(product);
			}
			
			return list;
		});
	}

	@Override
	public Optional<ProductVO> findById(long id) {
        String sql = "SELECT id, name, description FROM product WHERE id = ?";
        
		return jdbcTemplate.query(sql, new Object[]{id}, new int[]{java.sql.Types.INTEGER}, (ResultSet rs) -> {
			Optional<ProductVO> optionalProduct = Optional.empty();
			List<SkuVO> skusList = new ArrayList<>();

			if(rs.next()){
				String parentId = String.valueOf(rs.getLong(ID));
				String skusSql = "SELECT id, color, size, listprice, saleprice, quantityOnHand, smallImageUrl, mediumImageUrl, largeImageUrl FROM sku WHERE parentProduct_id = "+ parentId;
				
				jdbcTemplate.query(skusSql, (ResultSet skuRs) -> {
					
					
					while(skuRs.next()) {
						SkuVO skus = new SkuVO(
							skuRs.getLong(ID),
							skuRs.getString(COLOR),
							skuRs.getString(SIZE),
							skuRs.getDouble(LIST_PRICE),
							skuRs.getDouble(SALE_PRICE),
							skuRs.getLong(QUANTITY_ON_HAND),
							skuRs.getString(SMALL_IMAGE_URL),
							skuRs.getString(MEDIUM_IMAGE_URL),
							skuRs.getString(LARGE_IMAGE_URL)
						);
						skusList.add(skus);
					}
				});
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					skusList,
					commentService.getComments()
				);
				
				optionalProduct = Optional.of(product);
			}
			
			return optionalProduct;
		});
	}

	@Override
	public List<ProductVO> findByNameLike(String pattern) {
		String sql = "SELECT id, name, description FROM product WHERE name like ?";

		return jdbcTemplate.query(sql, new Object[]{"%" + pattern + "%"}, new int[]{java.sql.Types.VARCHAR}, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();
			List<SkuVO> skusList = new ArrayList<>();

			while(rs.next()){
				String parentId = String.valueOf(rs.getLong(ID));
				String skusSql = "SELECT id, color, size, listprice, saleprice, quantityOnHand, smallImageUrl, mediumImageUrl, largeImageUrl FROM sku WHERE parentProduct_id = "+ parentId;
				
				jdbcTemplate.query(skusSql, (ResultSet skuRs) -> {
					
					
					while(skuRs.next()) {
						SkuVO skus = new SkuVO(
							skuRs.getLong(ID),
							skuRs.getString(COLOR),
							skuRs.getString(SIZE),
							skuRs.getDouble(LIST_PRICE),
							skuRs.getDouble(SALE_PRICE),
							skuRs.getLong(QUANTITY_ON_HAND),
							skuRs.getString(SMALL_IMAGE_URL),
							skuRs.getString(MEDIUM_IMAGE_URL),
							skuRs.getString(LARGE_IMAGE_URL)
						);
						skusList.add(skus);
					}
				});
				
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					skusList,
					commentService.getComments()
				);
				
				list.add(product);
			}
			
			return list;
		});
	}

	@Override
	public ProductVO insert(ProductVO newProduct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ProductVO existingProduct) {
		
		String deleteProductSql = "DELETE FROM product WHERE id = "+ existingProduct.getId();
		jdbcTemplate.update(deleteProductSql);
		
		String deleteSkuSql = "DELETE FROM sku WHERE parentProduct_id = "+ existingProduct.getId();
		jdbcTemplate.update(deleteSkuSql);
	}

	@Override
	public void update(ProductVO existingProduct) {
		// TODO Auto-generated method stub

	}

}
