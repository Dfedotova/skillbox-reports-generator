package com.example.application.data.report;

import com.example.application.views.reportgeneration.ReportGenerationView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ExcelParsing {

    private final int month;
    private final int courseNumber;

    private final List<String> partnerCoursesNames = new ArrayList<>();
    private final List<Integer> partnerCoursesNumbers = new ArrayList<>();
    private final List<Double> sharesInPackages = new ArrayList<>();
    private final List<Double> proceedsInPackages = new ArrayList<>();
    private final List<Double> proceedsTotalsInPackages = new ArrayList<>();
    private final List<Double> refundsInPackages = new ArrayList<>();
    private final List<Double> refundsTotalsInPackages = new ArrayList<>();
    private final List<Integer> customerHWNum = new ArrayList<>();
    private final List<Integer> executorHWNum = new ArrayList<>();
    private final List<Integer> customerDiplomaNum = new ArrayList<>();
    private final List<Integer> executorDiplomaNum = new ArrayList<>();
    private final List<Double> customerHWCosts = new ArrayList<>();
    private final List<Double> customerDiplomaCosts = new ArrayList<>();
    private final List<Double> executorHWCosts = new ArrayList<>();
    private final List<Double> executorDiplomaCosts = new ArrayList<>();

    private final List<List<String>> partnerCoursesProceeds = new ArrayList<>();
    private final List<List<String>> partnerCoursesRefunds = new ArrayList<>();
    private final List<List<String>> partnerCoursesShares = new ArrayList<>();

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("sk", "SK"));

    public ExcelParsing(int month, int courseNumber) {
        this.month = month;
        this.courseNumber = courseNumber;
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }

    public void parseExcelTable() {
        FileProvider provider = new FileProvider();
        XSSFWorkbook wb = provider.readExcelFileFromDropbox("Отчёты по продажам и пакетам.xlsx");
        XSSFSheet sheet = wb.getSheetAt(0);
        getInfoFromRows(sheet);
    }

    private void getInfoFromRows(XSSFSheet sheet) {
        List<Integer> rows = findSearchedRows(sheet);
        if (rows.isEmpty())
            ReportGenerationView.showErrorNotification();
        else{
            getInfoFromNecessaryRows(rows, sheet, findColumnsNames(sheet));

            convertInfoToStringProceeds(rows);
            convertInfoToStringRefunds(rows);
            convertInfoToStringShares(rows);
        }
    }

    private List<Integer> findSearchedRows(XSSFSheet sheet) {
        List<Integer> rows = new ArrayList<>();
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            Cell cellMonth = row.getCell(1);
            Cell cellCourseNumber = row.getCell(3);

            if (cellMonth != null && cellMonth.getNumericCellValue() == month &&
                    cellCourseNumber.getNumericCellValue() == courseNumber)
                rows.add(i);
        }
        return rows;
    }

    private List<String> findColumnsNames(XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(0);
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < row.getLastCellNum(); i++)
            columnNames.add(row.getCell(i).getStringCellValue().trim().toLowerCase(Locale.ROOT));
        return columnNames;
    }

    private void getInfoFromNecessaryRows(List<Integer> rows, XSSFSheet sheet, List<String> columnNames) {
        for (int i = rows.get(0); i <= rows.get(rows.size() - 1); i++) {
            partnerCoursesNumbers.add((int) sheet.getRow(i).getCell(columnNames.indexOf("код"))
                    .getNumericCellValue());
            partnerCoursesNames.add(sheet.getRow(i).getCell(columnNames.indexOf("наименование курса / пакета"))
                    .getStringCellValue());
            sharesInPackages.add(sheet.getRow(i).getCell(columnNames.indexOf("доля в пакете"))
                    .getNumericCellValue());
            proceedsInPackages.add(sheet.getRow(i).getCell(columnNames.indexOf("выручка всего"))
                    .getNumericCellValue());
            proceedsTotalsInPackages.add(sheet.getRow(i).getCell(columnNames.indexOf("выручка по курсу"))
                    .getNumericCellValue());
            refundsInPackages.add(sheet.getRow(i).getCell(columnNames.indexOf("возвраты всего"))
                    .getNumericCellValue());
            refundsTotalsInPackages.add(sheet.getRow(i).getCell(columnNames.indexOf("возвраты по курсу"))
                    .getNumericCellValue());

            executorHWNum.add((int) sheet.getRow(i).getCell(columnNames.indexOf("проверка дз (кол-во)\nпартнер"))
                    .getNumericCellValue());
            customerHWNum.add((int) sheet.getRow(i).getCell(columnNames.indexOf("проверка дз (кол-во)\nне партн."))
                    .getNumericCellValue());
            executorDiplomaNum.add((int) sheet.getRow(i).getCell(columnNames.indexOf("проверка дип. (кол-во)\nпартнер"))
                    .getNumericCellValue());
            customerDiplomaNum.add((int) sheet.getRow(i).getCell(columnNames.indexOf("проверка дип. (кол-во)\nне партн."))
                    .getNumericCellValue());

            customerHWCosts.add(sheet.getRow(i).getCell(columnNames.indexOf("проверка дз (сумма)\nне партн."))
                    .getNumericCellValue());
            customerDiplomaCosts.add(sheet.getRow(i).getCell(columnNames.indexOf("проверка дип. (сумма)\nне партн."))
                    .getNumericCellValue());
            executorHWCosts.add(sheet.getRow(i).getCell(columnNames.indexOf("проверка дз (сумма)\nпартнер"))
                    .getNumericCellValue());
            executorDiplomaCosts.add(sheet.getRow(i).getCell(columnNames.indexOf("проверка дип. (сумма)\nпартнер"))
                    .getNumericCellValue());
        }
    }

    private void convertInfoToStringProceeds(List<Integer> rows) {
        for (int i = 0; i < rows.size(); i++)
            partnerCoursesProceeds.add(Arrays.asList(getCoursesFormatted().get(i), getSumsFormatted(proceedsInPackages).get(i),
                    getSharesFormatted().get(i), getSumsFormatted(proceedsTotalsInPackages).get(i)));
    }

    private void convertInfoToStringRefunds(List<Integer> rows) {
        for (int i = 0; i < rows.size(); i++)
            partnerCoursesRefunds.add(Arrays.asList(getCoursesFormatted().get(i), getSumsFormatted(refundsInPackages).get(i),
                    getSharesFormatted().get(i), getSumsFormatted(refundsTotalsInPackages).get(i)));
    }

    private void convertInfoToStringShares(List<Integer> rows) {
        for (int i = 0; i < rows.size(); i++)
            partnerCoursesShares.add(Arrays.asList(getCoursesFormatted().get(i), getSharesFormatted().get(i)));
    }

    private List<String> getCoursesFormatted() {
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < partnerCoursesNames.size(); i++)
            formatted.add(partnerCoursesNames.get(i) + ", №" + partnerCoursesNumbers.get(i).toString());
        return formatted;
    }

    private List<String> getSharesFormatted() {
        List<String> formatted = new ArrayList<>();
        for (double share : sharesInPackages)
            formatted.add(nf.format(share * 100) + "%");
        return formatted;
    }

    private List<String> getSumsFormatted(List<Double> sums) {
        List<String> formatted = new ArrayList<>();
        for (double sum : sums)
            formatted.add(nf.format(sum));
        return formatted;
    }

    // ГЕТТЕРЫ
    public int getRowsNumber() {
        return partnerCoursesNames.size();
    }

    public List<List<String>> getPartnerCoursesProceeds() {
        return partnerCoursesProceeds;
    }

    public List<List<String>> getPartnerCoursesRefunds() {
        return partnerCoursesRefunds;
    }

    public List<List<String>> getPartnerCoursesShares() {
        return partnerCoursesShares;
    }

    public double getProceedsSum() {
        return countSumDouble(proceedsInPackages);
    }

    public double getRefundsSum() {
        return countSumDouble(refundsInPackages);
    }

    public double getProceedTotalsSum() {
        return countSumDouble(proceedsTotalsInPackages);
    }

    public double getRefundTotalsSum() {
        return countSumDouble(refundsTotalsInPackages);
    }

    public int getCustomerHWNumber() {
        return countSumInteger(customerHWNum);
    }

    public int getExecutorHWNumber() {
        return countSumInteger(executorHWNum);
    }

    public int getCustomerDiplomaNumber() {
        return countSumInteger(customerDiplomaNum);
    }

    public int getExecutorDiplomaNumber() {
        return countSumInteger(executorDiplomaNum);
    }

    public double getCustomerHWCostsSum() {
        return countSumDouble(customerHWCosts);
    }

    public double getCustomerDiplomaCostsSum() {
        return countSumDouble(customerDiplomaCosts);
    }

    public double getExecutorHWCostsSum() {
        return countSumDouble(executorHWCosts);
    }

    public double getExecutorDiplomaCostsSum() {
        return countSumDouble(executorDiplomaCosts);
    }

    private double countSumDouble(List<Double> list) {
        double sum = 0.0;
        for (double s : list)
            sum += s;
        return sum;
    }

    private int countSumInteger(List<Integer> list) {
        int sum = 0;
        for (int s : list)
            sum += s;
        return sum;
    }
}
