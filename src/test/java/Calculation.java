import com.codeborne.xlstest.XLS;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.List;

public class Calculation {

    public void retrieveXlsxData(XLS xls, int sheetNumber, int rowCount, int columnCount, List<List<String[]>> array) {
        DataFormatter dataFormatter = new DataFormatter();

        for (int i = 0; i < rowCount; i++) {
            String[] column = new String[columnCount];
            for (int j = 0; j < columnCount; j++) {
                column[j] = dataFormatter.formatCellValue(
                        xls.excel.getSheetAt(sheetNumber).getRow(i).getCell(j)
                );
            }
            array.get(sheetNumber).add(i, column);
        }
    }
}
