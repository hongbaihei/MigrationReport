package com.cib.main.service;

/**
 * 身份证转换
 * @author Lou
 *
 */
public class IDCardUtil {
	
   public static String from15to18(String idCardNo15) {

       String centuryStr = "19";
       if(!(isIdCardNo(idCardNo15) && idCardNo15.length() == 15))
           throw new IllegalArgumentException("wrong id card!");

       int[] weight = new int[] {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

       String newNoBody = idCardNo15.substring(0, 6) + centuryStr + idCardNo15.substring(6);

       int checkSum = 0;
       for(int i=0; i< 17; i++) {
           int ai = Integer.parseInt("" + newNoBody.charAt(i));          
           checkSum = checkSum + ai * weight[i];
       }

       int checkNum = checkSum % 11;
       String checkChar = null;

       switch(checkNum) {
           case 0: checkChar = "1"; break;
           case 1: checkChar = "0"; break;
           case 2: checkChar = "X"; break;
           default: checkChar = "" + (12 - checkNum);
       }

       return newNoBody + checkChar;
   }


   public static String from18to15(String idCardNo18) {

       if(!(isIdCardNo(idCardNo18) && idCardNo18.length() == 18))
           throw new IllegalArgumentException("wrong id card!");

       return idCardNo18.substring(0, 6) + idCardNo18.substring(8, 17);
   }

   public static boolean isIdCardNo(String str) {

       if(str == null)
           return false;

       int len = str.length();
       if(len != 15 && len != 18)
           return false;

       for(int i=0; i<len; i++) {
           try {
               Integer.parseInt("" + str.charAt(i));
           }
           catch(NumberFormatException e) {
               return false;
           }
       }

       return true;
   }
   

}
