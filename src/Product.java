public class Product {
    private Long productId;
    private Long sellerId;
    private String productTitle;
    private String discount;
    private String minPrice;
    private String maxPrice;
    private String oriMinPrice;
    private String oriMaxPrice;
    private String productAverageStar;
    private String stock;
    private String productDetailUrl;
    private String productImage;

    public String toString() {
        return System.lineSeparator() + String.format(
                "%d,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                productId,
                sellerId,
                productTitle,
                discount,
                minPrice,
                maxPrice,
                oriMinPrice,
                oriMaxPrice,
                productAverageStar,
                stock,
                productDetailUrl,
                productImage
        );
    }
}
