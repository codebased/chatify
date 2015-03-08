package com.example.layoutdesign.DataSource;

import java.util.ArrayList;
import java.util.List;

import com.example.layoutdesign.Model.ContactNumber;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
 

public class DefaultContentProvider      {

	public static List<ContactNumber> getContacts(Context c) {
		 ContentResolver cr = c.getContentResolver();
         Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
         List<ContactNumber> result = new ArrayList<ContactNumber>();;

         if (cur.getCount() > 0) {
        	 
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                	
                	ContactNumber contact = new ContactNumber();
                	contact.setID(Long.parseLong(id));
                	contact.setName(name);
                    
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                           ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                           new String[]{id}, null);
                    while (pCur.moveToNext()) {
                          String phone = pCur.getString(
                                 pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                          
                        contact.getPhoneNumber().add(phone);
                    }
                    pCur.close();


                   Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                   
                    while (emailCur.moveToNext()) {
                     
                        String email = emailCur.getString(
                                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        //String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        
                        contact.getEmail().add(email);
                    }
                    emailCur.close();

                    // Get note.......
//                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
//                            ContactsContract.Data.MIMETYPE + " = ?";
//                    String[] noteWhereParams = new String[]{id,
//                    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//
//                     Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, 
//                             noteWhere, noteWhereParams, null);
//                    if (noteCur.moveToFirst()) {
//                        String note = noteCur.getString(
//                        noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
//                      System.out.println("Note " + note);
//                    }
//                    noteCur.close();

                    //Get Postal Address....

//                    String addrWhere = ContactsContract.Data.CONTACT_ID 
//                            + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//                    String[] addrWhereParams = new String[]{id,
//                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
//                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
//                                null, null, null, null);
//                    while(addrCur.moveToNext()) {
//                        String poBox = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//                        String street = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//                        String city = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//                        String state = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//                        String postalCode = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//                        String country = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//                        String type = addrCur.getString(
//                                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
//
//                        // Do something with these....
//
//                    }
//                    addrCur.close();
//
//                    // Get Instant Messenger.........
//                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " 
//                    + ContactsContract.Data.MIMETYPE + " = ?";
//                    String[] imWhereParams = new String[]{id,
//                        ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
//                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
//                            null, imWhere, imWhereParams, null);
//                    if (imCur.moveToFirst()) {
//                        String imName = imCur.getString(
//                                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
//                        String imType;
//                        imType = imCur.getString(
//                                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
//                    }
//                    imCur.close();
//
//                    // Get Organisations.........
//
//                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//                    String[] orgWhereParams = new String[]{id,
//                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
//                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
//                                null, orgWhere, orgWhereParams, null);
//                    if (orgCur.moveToFirst()) {
//                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
//                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
//                    }
//                    orgCur.close();

                    result.add(contact);
                } // has phone number
            } // while
            
            
       } // if
         return result;
    } // end of function 
}
