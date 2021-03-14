import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        Integer offset = 0;
        Integer limit = 20; //max 24

        //Задаем URL запроса за товарами (пока без offset и limit)
        String aliPageUrl =
                "https://gpsfront.aliexpress.com/getRecommendingResults.do?" +
                "callback=jQuery183017998643555551563_1615651502363&" +
                "widget_id=5547572&" +
                "platform=pc&" +
                "phase=1&" +
                "productIds2Top=&" +
                "postback=314d8cca-2e86-4430-9549-a8939ae02188&" +
                "_=1615651526857";

        //Задаем заголовки для столбцов csv файла
        String[] csvHeaders = {
                "productId",
                "sellerId",
                "productTitle",
                "discount",
                "minPrice",
                "maxPrice",
                "oriMinPrice",
                "oriMaxPrice",
                "productAverageStar",
                "stock",
                "productDetailUrl",
                "productImage"
        };

        String path = "products.csv";

        try {
            //Замеряем время парсинга и печатаем в конце
            long startTime = System.currentTimeMillis();

            //Записываем в csv файл данные парсинга, пердварительно получив csv строку со всеми товарами
            writeProductsToCsvFile(path, getCsvString(aliPageUrl, limit, offset, csvHeaders));
            long endTime = System.currentTimeMillis();
            System.out.println("Парсинг занял " + (endTime - startTime) + " мс");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getProductsAsJson(String aliPageUrl) throws IOException {
        URL url = new URL(aliPageUrl);
        URLConnection connection = url.openConnection();

        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();
        return result.toString();
    }

    private static String getCsvString(String aliPageUrl, Integer limit, Integer offset, String[] csvHeaders) throws IOException {
        StringBuilder csvString = new StringBuilder();
        csvString.append(String.join(",", csvHeaders));
        String aliPageUrlWithOffsetAndLimit;

        for (int i = 0; i < 5; i++) {
            //На каждой итерации делаем GET запрос с увеличенным смещением
            //Для этого реформируем URL запроса в функции и возвращаем
            aliPageUrlWithOffsetAndLimit = getAliPageUrlWithOffsetAndLimit(aliPageUrl, limit, offset);

            //Сам запрос и возврат товаров в JSON
            String responseString = getProductsAsJson(aliPageUrlWithOffsetAndLimit);

            //В начале строки с JSON находятся лишние данные, поэтому вырезаем подстроку с самим JSON содержимым
            String jsonString = responseString.substring(responseString.indexOf('(') + 1, responseString.lastIndexOf(')'));

            //Маппим к объектам Response и Product и получаем список товаров
            List<Product> productList = new Gson().fromJson(jsonString, Response.class).getResults();

            //Вставляем в StringBuilder
            productList.forEach(csvString::append);

            offset += 20;
        }

        return csvString.toString();
    }

    private static String getAliPageUrlWithOffsetAndLimit(String url, Integer limit, Integer offset) {
        return String.format("%s&limit=%d&offset=%d", url, limit, offset);
    }

    private static void writeProductsToCsvFile(String path, String csvString) throws IOException {
        Files.write(Paths.get(path), Collections.singleton(csvString));
    }
}
