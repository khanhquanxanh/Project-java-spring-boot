package com.Project1.demo.sevice.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Project1.demo.dto.request.ProductRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ProductDetailResponse;
import com.Project1.demo.model.Product;
import com.Project1.demo.repository.ProductRepository;
import com.Project1.demo.sevice.ProductService;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;
import com.Project1.demo.util.SlugUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	
	private final Path root = Paths.get("uploads/images");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(root);
    }

    /** Resize ảnh */
    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        int targetWidth = 500;
        int targetHeight = (originalImage.getHeight() * targetWidth) / originalImage.getWidth();

        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage output = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = output.createGraphics();
        graphics.drawImage(resultingImage, 0, 0, null);
        graphics.dispose();

        return output;
    }

    /** Lưu file ảnh */
    private String saveImage(MultipartFile file) throws IOException {

        // Tách tên file gốc
        String original = file.getOriginalFilename();
        if (original == null) original = "image.jpg";

        // Lấy baseName không chứa extension
        String baseName = original.replaceAll("[^a-zA-Z0-9]", "_");
        baseName = baseName.replaceFirst("[.][^.]+$", ""); // remove extension

        // Tạo fileName chuẩn .jpg
        String fileName = System.currentTimeMillis() + "_" + baseName + ".jpg";

        BufferedImage resized = resizeImage(file);
        Path path = root.resolve(fileName);

        ImageIO.write(resized, "jpg", path.toFile());

        return fileName;
    }


    /** Xóa file ảnh */
    private void deleteImage(String fileName) {
        try {
            Files.deleteIfExists(root.resolve(fileName));
        } catch (Exception ignored) {}
    }

	@Override
	public long saveAddProduct(ProductRequestDTO request) {


		Product product = Product.builder()
				.productName(request.getProductName())
				.group(request.getGroup())
				.price(request.getPrice())
				.brand(request.getBrand())
				.imageUrl(request.getImageUrl())
				.build();
		productRepository.save(product);
		return product.getId();
	}
	
	@Override
    public PageResponse getAllProduct(int pageNo, int pageSize, String... sorts) {

        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));

        Page<Product> products = productRepository.findAll(pageable);
        List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	@Override
	public PageResponse getFilterProductByPrice(int pageNo, int pageSize, Long minPrice, Long maxPrice,
			String... sorts) {
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
        
        Page<Product> products;

        if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	
	@Override
	public PageResponse getProductByBrand(int pageNo, int pageSize, Brands brand, String... sorts) {
		
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
		
		Page<Product> products = productRepository.findByBrand(brand, pageable);
		
		List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	@Override
	public PageResponse getProductByGroup(int pageNo, int pageSize, GroupProduct group, String... sorts) {
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
		
		Page<Product> products = productRepository.findByGroup(group, pageable);
		
		List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }
	
	/** ✔ THÊM SẢN PHẨM */
	@Override
	public Product addProduct(ProductRequestDTO dto, MultipartFile imageFile) {

	    try {

	        String slug = SlugUtils.toSlug(dto.getProductName());

	        // Nếu slug tồn tại → thêm số phía sau
	        int counter = 1;
	        String uniqueSlug = slug;
	        while (productRepository.existsBySlug(uniqueSlug)) {
	            uniqueSlug = slug + "-" + counter++;
	        }

	        String imageName = saveImage(imageFile);

	        Product product = Product.builder()
	                .productName(dto.getProductName())
	                .price(dto.getPrice())
	                .brand(dto.getBrand())
	                .group(dto.getGroup())
	                .slug(uniqueSlug)
	                .imageUrl("images/" + imageName)
	                .build();

	        return productRepository.save(product);

	    } catch (IOException e) {
	        throw new RuntimeException("Lỗi lưu ảnh sản phẩm: " + e.getMessage(), e);
	    }
	}

	@Override
	public Product updateProduct(Long id, ProductRequestDTO dto, MultipartFile imageFile) {

	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Product not found"));

	    try {

	        /* ===============================
	              UPDATE FIELDS IF PRESENT
	        =============================== */

	        if (dto.getProductName() != null && !dto.getProductName().isBlank()) {
	            product.setProductName(dto.getProductName());
	        }

	        if (dto.getGroup() != null) { // ENUM → chỉ cần check null
	            product.setGroup(dto.getGroup());
	        }

	        if (dto.getBrand() != null) { // ENUM → chỉ cần check null
	            product.setBrand(dto.getBrand());
	        }

	        if (dto.getPrice() != null) {
	            product.setPrice(dto.getPrice());
	        }

	        /* ===============================
	                HANDLE IMAGE UPDATE
	        =============================== */

	        if (imageFile != null && !imageFile.isEmpty()) {

	            Path uploadDir = Paths.get("uploads/images");

	            if (!Files.exists(uploadDir)) {
	                Files.createDirectories(uploadDir);
	            }

	            // Xóa ảnh cũ
	            if (product.getImageUrl() != null) {
	                Path oldImg = Paths.get("uploads/" + product.getImageUrl());
	                if (Files.exists(oldImg)) Files.delete(oldImg);
	            }

	            // Lưu ảnh mới
	            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
	            Path filePath = uploadDir.resolve(filename);

	            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	            product.setImageUrl("images/" + filename);
	        }

	        return productRepository.save(product);

	    } catch (Exception e) {
	        throw new RuntimeException("Update failed: " + e.getMessage());
	    }
	}



    @Override
    public void deleteProduct(Long id) {

        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // xóa file ảnh
            if (product.getImageUrl() != null) {
                File file = new File("uploads/" + product.getImageUrl().replace("images/", ""));
                if (file.exists()) file.delete();
            }

            productRepository.delete(product);

        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }
    
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }


}
