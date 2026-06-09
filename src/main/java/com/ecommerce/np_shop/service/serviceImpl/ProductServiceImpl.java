package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.ImageResponse;
import com.ecommerce.np_shop.dto.api.v1.ProductRequest;
import com.ecommerce.np_shop.dto.api.v1.ProductResponse;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.entity.Image;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.repo.CategoryRepository;
import com.ecommerce.np_shop.repo.ImageRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.ecommerce.np_shop.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ImageRepository imageRepository;
  private final String UPLOAD_ROOT_PATH = "upload";
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
    return getProductResponse(createAndSaveProduct(productRequest, file, category, productId));
  }

  @Override
  public List<ProductResponse> getProducts() {
    return productRepository.findAll().stream().map(this::getProductResponse).toList();
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
  public Product createAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category) {
    Product newProduct = new Product();
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    Image image = getImage(file);
    newProduct.addImage(image);
    image.setProduct(newProduct);
    newProduct.setCategory(category);
    return productRepository.save(newProduct);
  }

  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public Product createAndSaveProduct(
      ProductRequest product, MultipartFile file, Category category, UUID productId) {
    Product newProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    newProduct.setName(product.getName());
    newProduct.setDescription(product.getDescription());
    newProduct.setStock(product.getStock());
    newProduct.setPrice(product.getPrice());
    if (file != null) {
      Image image = imageRepository.findByFileName(file.getOriginalFilename()).orElse(null);
      if(image == null) {
        newProduct.addImage(getImage(file));
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
    productResponse.setImages(savedProduct.getImages().stream()
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
}
