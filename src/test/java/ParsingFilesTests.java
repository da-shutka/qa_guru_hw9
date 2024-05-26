import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.JiraTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ParsingFilesTests {

    private final ClassLoader cl = ParsingFilesTests.class.getClassLoader();

    @Test
    @DisplayName("Проверка содержимого CSV файла в ZIP архиве")
    void checkContentOfCsvFileInZipArchiveTest() throws Exception {
        List<String[]> expectedData = null;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("test_files.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));

                    expectedData = new ArrayList<String[]>();
                    expectedData.add(0, new String[]{"Мороженое;152.41;Москва"});
                    expectedData.add(1, new String[]{"Пирог;250.12;Симферополь"});
                    expectedData.add(2, new String[]{"Компот;35.00;Симферополь"});
                    List<String[]> actualData = csvReader.readAll();

                    Assertions.assertEquals(3, actualData.size());
                    Assertions.assertArrayEquals(expectedData.get(0), actualData.get(0));
                    Assertions.assertArrayEquals(expectedData.get(1), actualData.get(1));
                    Assertions.assertArrayEquals(expectedData.get(2), actualData.get(2));
                }
            }
        }
        Assertions.assertNotEquals(expectedData, null);
    }

    @Test
    @DisplayName("Проверка содержимого XLSX файла в ZIP архиве")
    void checkContentOfXlsxFileInZipArchiveTest() throws Exception {
        XLS xls = null;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("test_files.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    xls = new XLS(zis);

                    List<List<String[]>> expectedData = new ArrayList<>();
                    expectedData.add(0, new ArrayList<>());
                    expectedData.get(0).add(
                            0,
                            new String[]{"Номер", "Фамилия", "Имя", "Отчество", "Серия", "Номер", "Город", "ИНН"});
                    expectedData.get(0).add(
                            1,
                            new String[]{"1", "ПЕТРОВ", "ПЕТР", "ПЕТРОВИЧ", "3210", "012345", "Москва", "111222333444"});
                    expectedData.get(0).add(
                            2,
                            new String[]{"2", "ИВАНОВ", "ИВАН", "ИВАНОВИЧ", "5511", "123654", "Санкт-Петербург", "555666777888"});

                    expectedData.add(1, new ArrayList<>());
                    expectedData.get(1).add(0, new String[]{"Сотрудник", "Выплата"});
                    expectedData.get(1).add(1, new String[]{"1", "30000.15"});
                    expectedData.get(1).add(2, new String[]{"2", "45000.85"});


                    List<List<String[]>> actualData = new ArrayList<>();
                    actualData.add(0, new ArrayList<>());
                    actualData.add(1, new ArrayList<>());

                    for (int i = 0; i < expectedData.size(); i++) {
                        new Calculation().retrieveXlsxData(
                                xls,
                                i,
                                expectedData.get(i).size(),
                                expectedData.get(i).getFirst().length,
                                actualData
                        );
                    }

                    for (int i = 0; i < expectedData.size(); i++) {
                        for (int j = 0; j < expectedData.get(i).size(); j++) {
                            Assertions.assertArrayEquals(expectedData.get(i).get(j), actualData.get(i).get(j));
                        }
                    }
                }
            }
        }
        Assertions.assertNotEquals(xls, null);
    }

    @Test
    @DisplayName("Проверка содержимого PDF файла в ZIP архиве")
    void checkContentOfPdfFileInZipArchiveTest() throws Exception {
        PDF pdf = null;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("test_files.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    pdf = new PDF(zis);
                    String expectedResult = "Проверка знаний сотрудников компании \r\n" +
                            "Тестировщик - отлично \r\n" +
                            "Аналитик – отлично с минусом \r\n" +
                            "Разработчик – отлично \r\n";
                    String actualResult = pdf.text;

                    Assertions.assertEquals(expectedResult, actualResult);
                }
            }
        }
        Assertions.assertNotEquals(pdf, null);
    }

    @Test
    @DisplayName("Проверка содержимого JSON файла")
    void checkContentOfJsonFileTest() throws Exception {
        JiraTicket ticket;
        try (InputStream is = cl.getResourceAsStream("BE-623.json")
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            ticket = objectMapper.readValue(is, JiraTicket.class);

            Assertions.assertEquals("42457", ticket.getId());
            Assertions.assertEquals("https://jira.net/rest/api/2/issue/42457", ticket.getSelf());
            Assertions.assertEquals("BE-623", ticket.getKey());

            Assertions.assertEquals("19323", ticket.getFields().issuelinks[0].getId());
            Assertions.assertEquals("https://jira.net/rest/api/2/issueLink/19323",
                    ticket.getFields().issuelinks[0].getSelf());
            Assertions.assertEquals("10300", ticket.getFields().issuelinks[0].getType().getId());
            Assertions.assertEquals("Blocks", ticket.getFields().issuelinks[0].getType().getName());
            Assertions.assertEquals("is blocked by", ticket.getFields().issuelinks[0].getType().getInward());
            Assertions.assertEquals("blocks", ticket.getFields().issuelinks[0].getType().getOutward());
            Assertions.assertEquals("https://jira.net/rest/api/2/issueLinkType/10300",
                    ticket.getFields().issuelinks[0].getType().getSelf());

            Assertions.assertEquals("26548", ticket.getFields().issuelinks[1].getId());
            Assertions.assertEquals("https://jira.net/rest/api/2/issueLink/26548",
                    ticket.getFields().issuelinks[1].getSelf());
            Assertions.assertEquals("49855", ticket.getFields().issuelinks[1].getType().getId());
            Assertions.assertEquals("Depends on", ticket.getFields().issuelinks[1].getType().getName());
            Assertions.assertEquals("is depend on", ticket.getFields().issuelinks[1].getType().getInward());
            Assertions.assertEquals("depends on", ticket.getFields().issuelinks[1].getType().getOutward());
            Assertions.assertEquals("https://jira.net/rest/api/2/issueLinkType/49855",
                    ticket.getFields().issuelinks[1].getType().getSelf());
        }
        Assertions.assertNotEquals(ticket, null);
    }
}
