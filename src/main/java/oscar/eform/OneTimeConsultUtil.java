/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.eform;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.dao.EFormValueDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.EFormValue;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.UtilDateUtilities;

public class OneTimeConsultUtil{
	private static final Logger logger = MiscUtils.getLogger();

	
	private static EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
	private static EFormValueDao eFormValueDao = (EFormValueDao) SpringUtils.getBean("EFormValueDao");


	public static int[] getCDSReport(String cdsValue, String[] providerNo, String formId, String startStr, String endStr) {
		int[] count = new int[] {0,0};
		boolean issuePass =false;
		boolean datePass =false;
		boolean valuePass = false;
		boolean clientPass = false;
		List<EFormData> eformdata = eFormDataDao.findByFormIdProviderNo(Arrays.asList(providerNo),Integer.parseInt(formId));

	
		Date sDate = UtilDateUtilities.StringToDate(startStr);
		Date eDate = UtilDateUtilities.StringToDate(endStr);

		for(EFormData data : eformdata){
			List<EFormValue> eformValues = eFormValueDao.findByFormDataId(data.getId());	
			try{
				for(EFormValue value : eformValues){
					
					if(value.getVarName().equals("txtDate")){
						Date myDate = UtilDateUtilities.StringToDate(value.getVarValue());
						if(myDate.compareTo(sDate)>=0 && myDate.compareTo(eDate)<0)
							datePass = true;
					}
					
					if(value.getDemographicId() == 6306 || value.getDemographicId() == 6302 || value.getDemographicId() == 6301)
						clientPass = true;
					
					
				
					if(value.getVarName().equals("gender") || value.getVarName().equals("isAboriginal") || value.getVarName().equals("cboAge") || value.getVarName().equals("hasCto") || value.getVarName().equals("diagnosticCategories") || value.getVarName().equals("sourceOfReferral") || value.getVarName().equals("highestLevelEducation")){	
					
						if(value.getVarValue().equals(cdsValue)){
							valuePass=true;
						}else if(value.getVarValue().equals("")){
							if(value.getVarName().equals("cboAge"))
								if(cdsValue.equals("Unknown"))
									valuePass=true;

							if(value.getVarName().equals("gender"))
								if(cdsValue.equals("008-04"))
									valuePass=true;					
					
							if(value.getVarName().equals("isAboriginal"))
								if(cdsValue.equals("011-03"))
									valuePass=true;
							
							if(value.getVarName().equals("hasCto"))
								if(cdsValue.equals("015-03"))
									valuePass=true;
							
							if(value.getVarName().equals("diagnosticCategories"))
								if(cdsValue.equals("016-18"))
									valuePass=true;
							
							if(value.getVarName().equals("sourceOfReferral"))
								if(cdsValue.equals("018-25"))
									valuePass=true;
							
							if(value.getVarName().equals("highestLevelEducation"))
								if(cdsValue.equals("29a-08"))
									valuePass=true;							
						}	
					}else if(cdsValue.equals("017-12")){
						issuePass=true;
						for(int i=1; i<13 ;i++){
							if(i<10){
								if(value.getVarName().equals("017-0" + i)){
									valuePass = true;
									break;
								}
							}else{
								if(value.getVarName().equals("017-" + i)){
									valuePass = true;
									break;
								}
							}
						}	
					}else if(cdsValue.equals(value.getVarName())){
						if(value.getVarValue().equals("true")){
							valuePass = true;
						}
						
						
					}					
				}
				
				if(issuePass){
					if(datePass && !valuePass){
						if(clientPass){
							count[0]++;
						}
						else{
							
							count[1]++;	
						}
					}					
				}else{				
					if(datePass && valuePass){
						if(clientPass){
							count[0]++;
						}
						else{
							count[1]++;
						}
					}
				}

				issuePass=false;
				datePass=false;
				valuePass=false;
				clientPass = false;

			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}

		}

		return count;	
	}

	public static int[] getOSISReport(String activity, String moved, String age, String gender, String living, String aboriginal, String[] providerNo, String formId, String startStr, String endStr) {
		
		int[] count = new int[] {0,0};

		List<EFormData> eformdata = eFormDataDao.findByFormIdProviderNo(Arrays.asList(providerNo),Integer.parseInt(formId));

		Date sDate = UtilDateUtilities.StringToDate(startStr);
		Date eDate = UtilDateUtilities.StringToDate(endStr);


		for(EFormData data : eformdata){
			
			List<EFormValue> eformValues = eFormValueDao.findByFormDataId(data.getId());
				
			try{
				boolean clientPass = false, agePass = false, genderPass = false, livingPass = false, aboriginalPass = false, movePass = false, datePass = false, activityPass = false;
				
				for(EFormValue value : eformValues){
					if(value.getVarName().equals("txtDate")){
						Date myDate = UtilDateUtilities.StringToDate(value.getVarValue());


						if(myDate.compareTo(sDate)>=0 && myDate.compareTo(eDate)<0){
							datePass = true;
						}
					}
		
					if(value.getDemographicId() == 6306 || value.getDemographicId() == 6302 || value.getDemographicId() == 6301){
						clientPass = true;
					}

					if(value.getVarName().equals("gender")){
						if(value.getVarValue().equals(gender)){
							genderPass = true;
						//If the gender is blank it will check if it's requesting the unknown gender and set the flag to true
						}else if(value.getVarValue().equals("")){
							if(gender.equals("008-04")){
								genderPass = true;
							}
						}
					}			

					if(value.getVarName().equals("MovedFrom")){
						if(value.getVarValue().equals(moved)){
							movePass = true;
						}
					}

					if(value.getVarName().equals("LivingSituation")){
						if(value.getVarValue().equals(living))
							livingPass = true;
					}	

					
					if(value.getVarName().equals("isAboriginal")){
						if(value.getVarValue().equals(aboriginal))
							aboriginalPass = true;
					}	

					if(value.getVarName().equals("cboAge")){
						if(age.equals("15-21")){
							if(value.getVarValue().equals("0-15") || value.getVarValue().equals("16-17") || value.getVarValue().equals("18-24"))
								agePass=true;
						}else if(age.equals("22-30")){
							if(value.getVarValue().equals("25-34"))
								agePass=true;
						}else if(age.equals("31-45")){						
							if(value.getVarValue().equals("35-44"))
								agePass=true;	
						}else if(age.equals("46-64")){
							if(value.getVarValue().equals("45-54") || value.getVarValue().equals("55-64"))
								agePass=true;
						}else if(age.equals("65+")){
							if(value.getVarValue().equals("65-74") || value.getVarValue().equals("75-84") || value.getVarValue().equals("85 and over"))
								agePass=true;
						}else if(age.equals("unknown") || age.equals("")){
							if(value.getVarValue().equals("Unknown"))
								agePass=true;
						}


					}

					if(value.getVarName().equals(activity)){
						activityPass=true;
					}
				}
				


				if(moved.equals("") && aboriginal.equals("") && activity.equals("")){
					if(agePass && genderPass && livingPass && datePass){
						if(clientPass)
							count[0]++;
						else
							count[1]++;
					}
				}else if(moved.equals("") && age.equals("") && activity.equals("")){
					if(genderPass && livingPass && aboriginalPass && datePass){
						if(clientPass)
							count[0]++;
						else
							count[1]++;
					}
				}else if(age.equals("") && gender.equals("") && living.equals("") && activity.equals("") && aboriginal.equals("")){
					if(movePass && datePass){
						if(clientPass)
							count[0]++;
						else
							count[1]++;
					}
				}else if(!activity.equals("")){
					if(datePass && activityPass){
						if(clientPass)
							count[0]++;
						else
							count[1]++;
					}
				}

				activityPass=false;
				genderPass = false;
				livingPass = false;
				agePass = false;
				aboriginalPass = false;
				movePass = false;
				datePass = false;
				clientPass = false;

				
		
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}

		}


		return count;
	}

	

}
