package com.cerner.hi.pitc.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.cerner.hi.pitc.model.result.ValidationResult;
import com.cerner.hi.pitc.repository.ConfigurationRepository;
import com.cerner.hi.pitc.util.Constants;
import com.cerner.hi.pitc.util.Constants.ValidationType;
import com.cerner.hi.pitc.export.SendEmail;


public class ExcelWriter {
	XSSFWorkbook workbook = null;
	List<String> excelColumnList = null;
	List<String> validatorExcelCols =new ArrayList<String>();
	File filename = null;
	int rowNumber = 0;
	int columnCount = 0;
	private ConfigurationRepository dao = new ConfigurationRepository();
	// Create row object
	

	public void writeToExcel(JIRAComplianceList complianceList, String configId)
			throws IOException, URISyntaxException {
		workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet("Jira Validation");
		Configuration configuration = dao.get(configId);
		// Adding column names dynamically
		
		ArrayList<JIRACompliance> compList=complianceList.getJiraComplianceList();
		Map<String, Integer> excelColumnIndexMap = new LinkedHashMap<String, Integer>();
	
		for(int i=0;i<compList.size();i++) {
			JIRACompliance comp1 = complianceList.getJiraComplianceList().get(i);	
			excelColumnList = comp1.getValidationResult().getValidationInfoList().stream().map(s -> s.getExcelColumn())
					.collect(Collectors.toList());
			if( !excelColumnList.isEmpty() ) {
				validatorExcelCols = excelColumnList;	
			}	
		}

		System.out.println("validatorExcelColumns:"+validatorExcelCols);

		populatecolumnIndex(excelColumnIndexMap, validatorExcelCols,configId);
		// Creating the column with names
		Cell cellValue;
		XSSFRow row = spreadsheet.createRow(rowNumber);
		for (String str : excelColumnIndexMap.keySet()) {
			cellValue = row.createCell(excelColumnIndexMap.get(str));
			cellValue.setCellStyle(getHeadingStyle());
			cellValue.setCellValue(str);
		}

		for (JIRACompliance compliance : complianceList.getJiraComplianceList()) {

			row = spreadsheet.createRow(++rowNumber);

			populateColumnValues(row, compliance.getValidationResult(), excelColumnIndexMap);
		}
		
		spreadsheet.setAutoFilter(new CellRangeAddress(0, --rowNumber, 1, excelColumnIndexMap.size() - 1));
		spreadsheet.createFreezePane(0, 1);
		
		for(int i=1;i<excelColumnIndexMap.size();i++)
			spreadsheet.autoSizeColumn(i);
		
		ConfigurationRepository dao = new ConfigurationRepository();
		Configuration config = dao.get(configId);
		
		if(Constants.ValidationType.MILLENIUM == config.getTypeOfValidation()) {
		//to create basic info sheet
		XSSFSheet spreadsheet1 = workbook.createSheet("Validation Info");
		
		spreadsheet1.setColumnWidth(0, 256*20);
		spreadsheet1.setColumnWidth(1, 256*40);
		spreadsheet1.setColumnWidth(2, 256*40);
		spreadsheet1.setColumnWidth(3, 256*40);
		
		  XSSFRow rowhead1 = spreadsheet1.createRow((short)0);
          rowhead1.createCell(0).setCellValue("Value");
          rowhead1.createCell(1).setCellValue("Description");
          rowhead1.getCell(0).setCellStyle(getHeadingStyle());
          rowhead1.getCell(1).setCellStyle(getHeadingStyle());
         
          XSSFRow firstrow = spreadsheet1.createRow((short)1);
          firstrow.createCell(0).setCellStyle(getStyleForValue("N"));
          firstrow.getCell(0).setCellValue("N");          
          firstrow.createCell(1).setCellValue("NO(Violation)");
          
          XSSFRow secondrow = spreadsheet1.createRow((short)2);
          secondrow.createCell(0).setCellStyle(getStyleForValue("Y"));
          secondrow.getCell(0).setCellValue("Y");          
          secondrow.createCell(1).setCellValue("YES(As per the process)");

          XSSFRow thirdrow = spreadsheet1.createRow((short)3);
          thirdrow.createCell(0).setCellStyle(getStyleForValue("P"));
          thirdrow.getCell(0).setCellValue("P");          
          thirdrow.createCell(1).setCellValue("Partial (only one person has given a +1 )");
          
          XSSFRow thirdrow1 = spreadsheet1.createRow((short)4);
          thirdrow1.createCell(0).setCellStyle(getStyleForValue("N/A"));
          thirdrow1.getCell(0).setCellValue("N/A");          
          thirdrow1.createCell(1).setCellValue("Not Applicable(project specific)");
          
          XSSFRow thirdrow2 = spreadsheet1.createRow((short)5);
          thirdrow2.createCell(0).setCellStyle(getStyleForValue("N/A"));
          thirdrow2.getCell(0).setCellValue("NC");          
          thirdrow2.createCell(1).setCellValue(" Jira NOT CLOSED ");
		
          XSSFRow rowhead2 = spreadsheet1.createRow((short)6);
          rowhead2.createCell(0).setCellValue("Validators");
          rowhead2.createCell(1).setCellValue("Y if(Criteria)");
          rowhead2.createCell(2).setCellValue("N if(Criteria)");
          rowhead2.createCell(3).setCellValue("Naming Convention of JIRA");
      
          rowhead2.getCell(0).setCellStyle(getHeadingStyle());
          rowhead2.getCell(1).setCellStyle(getHeadingStyle());
          rowhead2.getCell(2).setCellStyle(getHeadingStyle());
          rowhead2.getCell(3).setCellStyle(getHeadingStyle());
      
          XSSFRow forthrow = spreadsheet1.createRow((short)7);
          forthrow .createCell(0).setCellValue("REQ SUBTASK:+1");            
          forthrow .createCell(1).setCellValue("Y if In Comments +1 from Developer and Tester");
          forthrow .createCell(2).setCellValue("N if no +1 from Developer and Tester obtained from subtask[Developemnt and Testcase and CG]");
          forthrow .createCell(3).setCellValue("Requirements/ requirements"); 
          
          XSSFRow fifthrow = spreadsheet1.createRow((short)8);
          fifthrow .createCell(0).setCellValue("REQ SUBTASK:JAZZLINK");            
          fifthrow .createCell(1).setCellValue("Y if Description / comments contains REQ: Jazz link ");
          fifthrow .createCell(2).setCellValue("N if Description / comments DOES NOT contain REQ: Jazz link ");
          fifthrow .createCell(3).setCellValue("Requirements/ requirements ");
          
          XSSFRow row6 = spreadsheet1.createRow((short)9);
          row6 .createCell(0).setCellValue("TECH DESIGN");            
          row6 .createCell(1).setCellValue("Y if main/sub jira consists of an attachment named TechDoc OR linksTo section comprises of crucible/wiki.cerner link AND Crucible link consists of the attachment named TechDoc. ");
          row6 .createCell(2).setCellValue("N if subtask with TechDesign name is not created OR naming convention is not followed(TechDoc) OR Attachment is not present in the crucible link ");
          row6.createCell(3).setCellValue("Tech Design ");
          
          XSSFRow row7 = spreadsheet1.createRow((short)10);
          row7 .createCell(0).setCellValue("DEVELOPMENT");            
          row7 .createCell(1).setCellValue("Y if main/sub jira consists of an crucible link under jira comments or linksTo section AND CrucibleClosedTime must be less than the SubtaskJiraClosedTime [CrucibleClosedTime < SubTaskClosedTime]");
          row7 .createCell(2).setCellValue("N if crucible link/github link is not provided in the mainJira/subTaskjira OR subTaskjira is closed before crucible link is closed.");
          row7 .createCell(3).setCellValue("Development"); 
          
          XSSFRow row8 = spreadsheet1.createRow((short)11);
          row8 .createCell(0).setCellValue("TESTCASE SUBTASK:+1");            
          row8 .createCell(1).setCellValue("Y In Comments +1 from Developer and SD");
          row8 .createCell(2).setCellValue("N if Naming convention not followed(Testcase and CG)and SD and Developer obtained from the subtasks[Requirements and Development] has not provided +1 ");
          row8.createCell(3).setCellValue("Test Case and CG");
          
          XSSFRow row9 = spreadsheet1.createRow((short)12);
          row9 .createCell(0).setCellValue("TESTCASE SUBTASK:RQM");            
          row9 .createCell(1).setCellValue("Y if RQM link(Jazz link itself) is provided in the linkTOsection/Comments/description");
          row9.createCell(2).setCellValue("N if RQM link (Jazz link itself)is not provided");
          row9.createCell(3).setCellValue("Test Case and CG");
          
          XSSFRow row10 = spreadsheet1.createRow((short)13);
          row10 .createCell(0).setCellValue("ENG/CERTIFICATION TESTING");            
          row10 .createCell(1).setCellValue("Y if RQM link is provided in the linkTOsection/Comments");
          row10 .createCell(2).setCellValue("N if RQM link is not provided");
          row10.createCell(3).setCellValue("Engineering testing / EBB / Certification testing / Certification");
          
          XSSFRow row11 = spreadsheet1.createRow((short)14);
          row11 .createCell(0).setCellValue("ENG 18.02 OR INTGM");            
          row11 .createCell(1).setCellValue("Y if RQM link is provided in the linkTOsection/Comments");
          row11.createCell(2).setCellValue("N if RQM link is not provided");
          row11.createCell(3).setCellValue("ENG 18.02/INTGM"); 
		}   
		String fileNamePostFix = Calendar.getInstance().getTime().toString().replaceAll(":", "");
		FileOutputStream out;
		try {
			filename = new File("results/" + configId + "_" + fileNamePostFix + ".xlsx");
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			new File("results/").mkdirs();
			out = new FileOutputStream(new File("results/" + configId + "_" + fileNamePostFix + ".xlsx"));
		}
		workbook.write(out);
		
		//check for email id field
		if(configuration.getRecipientEmailID()!="" && configuration.getSenderEmailID()!="")
		{
			try {
				SendEmail emailObj = new SendEmail();
				emailObj.sendEmail(configuration.getSenderEmailID(),configuration.getRecipientEmailID(),filename.getName());
			}
			catch(MessagingException m)
			{
				m.printStackTrace(); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		out.close();
		workbook = null;
	}

	private void populateColumnValues(XSSFRow row, ValidationResult compliance, Map<String, Integer> excelColumnIndexMap) throws URISyntaxException {
		
		for(String columnName : excelColumnIndexMap.keySet()) {
			int index = excelColumnIndexMap.get(columnName);
			String columnValue = getValueFromResult(compliance,columnName);
			XSSFCell cellValue = row.createCell(index);
			cellValue.setCellStyle(getStyleForValue(columnValue));
			cellValue.setCellValue(columnValue);
			
		}
	}

	private String getValueFromResult(ValidationResult compliance, String columnName) throws URISyntaxException {
	
		switch(columnName) {
		    case "#" : String columnNumber = "" + ++columnCount;
		    	       return columnNumber;
			case "JIRA-ID" : return getString(compliance.getJiraNumber());
			case "JIRA SUMMARY" : return getString(compliance.getJiraSummary());
			case "EPIC/CAPABILITY" : return getString(compliance.getEpic());
			case "ISSUE TYPE" : return getString(compliance.getIssueType());
			case "STATUS" : return getString(compliance.getStatus());
			case "RESOLUTION" : return getString(compliance.getResolution());
			case "REQ ID" : return getString(compliance.getReqID());
			case "SOLUTION DETAIL" : return getString(compliance.getSolutionDetail());
			case "JIRA GROUP" : return getString(compliance.getJiraGroup());
			case "COMPONENTS" : return (getString(compliance.getComponents())).toString();
			case "CLIENT VIEWABLE" : return getString(compliance.getClientViewable());			
			case "VERIFIER" : return getString(compliance.getTester());
			case "ASSIGNEE" : return getString(compliance.getAssignee());
			case "REPORTER" : return getString(compliance.getReporter());
			case "SME" : return getString(compliance.getSme());
			case "PLANNED VERSION" : return getString(compliance.getPlannedVersion());
			case "FIX VERSION" : return getString(compliance.getFixVersion());
			case "VERIFIED VERSION" : return getString(compliance.getVerifiedVersion());
			case "SOL. DESIGNER" : return getString(compliance.getSolDesigner());
			case "DEVELOPER" : return getString(compliance.getDeveloper());
			default : Optional<ValidationInfo> infoOptional = compliance.getValidationInfoList().stream().filter(i -> i.getExcelColumn().equals(columnName)).findFirst();
			          if(infoOptional.isPresent()) {
			        	  ValidationInfo info = infoOptional.get();
			        	  return getString(info);
			          }
			          return "";
		}
	}

	private void populatecolumnIndex(Map<String, Integer> excelColumnIndexMap, List<String> excelColumnList, String configId) throws FileNotFoundException {
		ValidationType typeOfValidation;
		ConfigurationRepository dao = new ConfigurationRepository();
		Configuration config = dao.get(configId);
		typeOfValidation=config.getTypeOfValidation();
		int i = -1;
	
		excelColumnIndexMap.put("#", ++i);
		excelColumnIndexMap.put("JIRA-ID", ++i);
		excelColumnIndexMap.put("JIRA SUMMARY", ++i);		
		excelColumnIndexMap.put("ISSUE TYPE", ++i);
		excelColumnIndexMap.put("STATUS", ++i);	
		excelColumnIndexMap.put("RESOLUTION", ++i);	
		excelColumnIndexMap.put("SOLUTION DETAIL", ++i);
		excelColumnIndexMap.put("JIRA GROUP", ++i);
		excelColumnIndexMap.put("COMPONENTS", ++i);
		excelColumnIndexMap.put("CLIENT VIEWABLE", ++i);
		excelColumnIndexMap.put("VERIFIER", ++i);
		excelColumnIndexMap.put("REPORTER", ++i);
		excelColumnIndexMap.put("ASSIGNEE", ++i);
		
		
		if((Constants.ValidationType.SOARIAN == typeOfValidation)||(Constants.ValidationType.HEALTHIEINTENT == typeOfValidation)) {
			excelColumnIndexMap.put("PLANNED VERSION", ++i);
			excelColumnIndexMap.put("VERIFIED VERSION", ++i);
			excelColumnIndexMap.put("FIX VERSION", ++i);
			excelColumnIndexMap.put("SOL. DESIGNER", ++i);
			excelColumnIndexMap.put("DEVELOPER", ++i);
			excelColumnIndexMap.put("EPIC/CAPABILITY", ++i);
			excelColumnIndexMap.put("REQ ID", ++i);
			excelColumnIndexMap.put("SME", ++i);
		}

		for (String s : excelColumnList) {
			excelColumnIndexMap.put(s, ++i);
		}
	}

	private Object getString(ArrayList<String> components) {
		StringBuilder sb = new StringBuilder();
		components.forEach(s -> sb.append(s));
		return sb.toString();
	}

	private String getString(final BasicUser user) {
		if (null != user) {
			if (null != user.getDisplayName() || user.getDisplayName() != "")
				return user.getDisplayName() + "(" + user.getName() + ")";
		}
		return "N";
	}

	private String getString(final String str) {
		if (!"".equals(str))
			return str;
		return "N";
	}

	private String getString(final ValidationInfo info) {
		if (null == info.getInfo().getValue())
			return "N/A";
		return info.getInfo().getValue();
	}


	private CellStyle getStyleForValue(final String value) {

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		if(null!=value) {
			if (value.equals("N")) {
				cellStyle.setFillForegroundColor(HSSFColorPredefined.RED.getIndex());
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}else if(value.equals("P")) {
				cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
		}

		return cellStyle;
	}

	private CellStyle getHeadingStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setRotation((short)0);
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setShrinkToFit(true);
		cellStyle.setWrapText(true);
		//cellStyle.setRotation((short)90);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		//cellStyle.setAlignment(HorizontalAlignment);
		cellStyle.setBorderLeft(BorderStyle.THICK);
		cellStyle.setBorderBottom(BorderStyle.THICK);
		cellStyle.setBorderRight(BorderStyle.THICK);
		cellStyle.setBorderTop(BorderStyle.THICK);
		//cellStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);

		return cellStyle;
	}

}
