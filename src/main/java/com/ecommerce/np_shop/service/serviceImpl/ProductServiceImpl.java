package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.*;
import com.ecommerce.np_shop.entity.*;
import com.ecommerce.np_shop.repo.*;
import com.ecommerce.np_shop.service.ProductService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ReviewRepository reviewRepository;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ImageRepository imageRepository;
  private final String UPLOAD_ROOT_PATH = "upload";
  private final AccountRepository accountRepository;

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public ProductResponse createProduct(ProductRequest product, MultipartFile file) {
    if (file.isEmpty()) {
      throw new RuntimeException("Image file is empty");
    }
    Category category =
        categoryRepository
            .findById(product.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
    return getProductResponse(createAndSaveProduct(product, file, category));
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public ProductResponse updateProduct(
      ProductRequest productRequest, MultipartFile file, UUID productId) {
    Category category =
        categoryRepository
            .findById(productRequest.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
    return getProductResponse(updateAndSaveProduct(productRequest, file, category, productId));
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public ProductResponse addImage(MultipartFile file , UUID productId){
    return getProductResponse(addImageToProduct(file,productId));
  }

  @Override
  public Page<ProductResponse> getProducts(Pageable pageable) {
    return productRepository.findAll(pageable).map(this::getProductResponse);
  }

  @Override
  public ProductResponse getProduct(UUID productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    return getProductResponse(product);
  }

  @Override
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public void deleteProduct(UUID productId) {
    Product product = checkProductExistsAndGetProduct(productId);
    for (Image image : product.getImages()) {
      removeFile(image.getUrl());
    }
    productRepository.deleteById(productId);
  }


  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public Product addImageToProduct(
          MultipartFile file, UUID productId) {
    if(file.isEmpty()) throw new RuntimeException("Image file is empty");
    Product newProduct =
            productRepository
                    .findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

      Image image = imageRepository.findByFileName(file.getOriginalFilename()).orElse(null);
      if(newProduct.getImages().size()>=5){
        throw new RuntimeException("Image upload max reached");
      }
      if(image == null) {
        newProduct.addImage(getImage(file));
      }else{
        throw new RuntimeException("Image already exists");
      }
    return productRepository.save(newProduct);
  }

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public Product createAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category) {
    Product newProduct = new Product();
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    Image image = getImage(file);
    newProduct.setMainImageUrl(image.getUrl());
    newProduct.addImage(image);
    image.setProduct(newProduct);
    newProduct.setCategory(category);
    return productRepository.save(newProduct);
  }

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public Product updateAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category, UUID productId) {
    Product newProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    if(newProduct.getReserveStock() > product.getStock()){
      throw new RuntimeException("Insufficient stock , cause there's a reserve stock for incoming payments");
    }
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    if (file != null) {
      Image image = imageRepository.findByFileName(file.getOriginalFilename()).orElse(null);
      if(image == null) {
        Image newImage = getImage(file);
        removeFile(newProduct.getMainImageUrl());
        newProduct.setMainImageUrl(newImage.getUrl());
        newProduct.addImage(newImage);
      }
    }
    newProduct.setCategory(category);
    return productRepository.save(newProduct);
  }

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public void deleteImage(UUID imageId) {
    Image image =  imageRepository
        .findById(imageId)
        .orElseThrow(() -> new RuntimeException("Image not found"));
    removeFile(image.getUrl());
    imageRepository.delete(image);
  }

  public Product checkProductExistsAndGetProduct(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
  }

  private ProductResponse getProductResponse(Product savedProduct) {
    ProductResponse productResponse = new ProductResponse();
    productResponse.setId(savedProduct.getId());
    productResponse.setName(savedProduct.getName());
    productResponse.setDescription(savedProduct.getDescription());
    productResponse.setStock(savedProduct.getStock());
    productResponse.setPrice(savedProduct.getPrice());
    productResponse.setOverAllRating(savedProduct.getOverAllRating());
    productResponse.setNumberOfReviews(savedProduct.getReviews().size());
    productResponse.setCreatedAt(savedProduct.getCreatedAt());
    Image mainImage = savedProduct.getImages().stream()
            .filter(image -> image.getUrl().equals(savedProduct.getMainImageUrl()))
            .findFirst().orElse(null);
    if(mainImage == null) {
      mainImage = savedProduct.getImages().getFirst();
      savedProduct.setMainImageUrl(mainImage.getUrl());
    }
    productResponse.setMainImage(getImageResponse(mainImage));
    productResponse.setImages(savedProduct.getImages().stream()
                    .filter(image -> !image.getUrl().equals(savedProduct.getMainImageUrl()))
                    .map(this::getImageResponse)
            .toList());
    productResponse.setCategoryId(savedProduct.getCategory().getId());
    return productResponse;
  }

  private ImageResponse getImageResponse(Image image) {
    return ImageResponse.builder()
            .id(image.getId())
            .url(image.getUrl())
            .fileName(image.getFileName())
            .contentType(image.getContentType())
            .build();
  }

  private String getImageUrl(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null
        || (!originalFilename.endsWith(".png")
            && !originalFilename.endsWith(".jpg")
            && !originalFilename.endsWith(".jpeg"))) {
      throw new RuntimeException("Only these file format is available .png .jpeg .jpg");
    }
    String fileName = UUID.randomUUID() + "-" + originalFilename;
    Path path = Paths.get(UPLOAD_ROOT_PATH, fileName);
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileName;
  }
  private void removeFile(String imageUrl){
    Path path = Paths.get(UPLOAD_ROOT_PATH + "/" +Paths.get(imageUrl));
    if (Files.exists(path)) {
      try {
        Files.delete(path);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
  public boolean checkProductStatus(UUID productId) {
    return  productRepository.existsById(productId);
  }
  private Image getImage(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String contentType = file.getContentType();
    String url = getImageUrl(file);
    Image image = new Image();
    image.setUrl(url);
    image.setFileName(originalFilename);
    image.setContentType(contentType);
    return image;
  }

  public Resource getImageResource(String url) throws MalformedURLException {
    Path path = Paths.get(UPLOAD_ROOT_PATH + "/" +Paths.get(url));
    Resource resource = new UrlResource(path.toUri());
    if (!resource.exists() || !resource.isReadable()) {
      throw new RuntimeException("Image not found");
    }
    return resource;
  }
  public Page<ProductResponse> search(Pageable pageable , String keyword) {
    return productRepository.search(keyword, pageable).map(this::getProductResponse);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  @Transactional
  public ReviewResponse postReview(UUID accountId, ReviewRequest reviewRequest) {

    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    Product product =
        productRepository
            .findById(reviewRequest.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
    Review review =
        Review.builder()
            .rating(reviewRequest.getRating())
            .product(product)
            .description(reviewRequest.getDescription())
            .account(account)
            .build();
    product.getReviews().add(review);
    product.calculateOverAllRating();
    productRepository.save(product);
    return getReviewResponse(review);
  }

  @Override
  public Page<ReviewResponse> getReviews(Pageable page, UUID productId) {
    return reviewRepository.getAllReviewByProductId(page, productId).map(this::getReviewResponse);
  }

  private ReviewResponse getReviewResponse(Review review) {
    return ReviewResponse.builder()
        .id(review.getId())
        .rating(review.getRating())
        .productId(review.getProduct().getId())
        .createdAt(review.getCreatedAt())
        .description(review.getDescription())
        .accountUsername(review.getAccount().getUsername())
        .build();
  }
}
