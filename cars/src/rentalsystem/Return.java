package rentalsystem;

import com.mysql.jdbc.Statement;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


 

/**
 * Servlet implementation class Return
 */
@WebServlet("/Return")
public class Return extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		
	      PrintWriter out = response.getWriter();
          
          String resid = request.getParameter("resid");
          String cid = request.getParameter("cid");
      
          String resid1= null;
          String cid1 = null;
          String totalrent= null;
          String email= null;
          String pickup= null;
          String dropoff= null;
          String licenseno= null;
          String name= null;


	
	     try
	      {
	         Class.forName("com.mysql.jdbc.Driver");
	         Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/car", "root", "");
	         String query = "select * from reservation where resid ="+resid;
	         Statement st = (Statement) con.createStatement();
	         ResultSet rs = st.executeQuery(query);
	 
	         while(rs.next())
	         {
	        	 resid1 = rs.getString("resid");
	        	 cid1 = rs.getString("carid");
	        	 totalrent = rs.getString("totalrent");
	        	 email = rs.getString("email");
	        	 pickup = rs.getString("pickup");
	        	 dropoff = rs.getString("dropoff");
	        	 licenseno = rs.getString("licenseno");
	        	 name = rs.getString("name");
	           // response.getWriter().write(rs.getString("resid") + " - " + rs.getString("totalrent") + " ");
	         }
	         
	         //code to reflect changes in the rentcar table
	         
             PreparedStatement ps = con.prepareStatement("UPDATE rentcar SET cust_email= ? ,cust_passport= ? ,status= ? where carid ="+cid1);
             ps.setString(1, "null");
             ps.setString(2, "null");
             ps.setString(3, "NOT BOOKED");
            ps.executeUpdate();
	 		
            //recording history
//            java.util.Date date = new java.util.Date();
//           String returndate= date.toString();

			  Date returnDate = new Date();


			  SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  String returnDateString = mysqlDateFormat.format(returnDate);
           PreparedStatement ps1 = con.prepareStatement("insert into history values(?,?,?,?,?,?,?,?)");
            
            ps1.setString(1, returnDateString);
            ps1.setString(2, name);
            ps1.setString(3, cid1);
            ps1.setString(4, totalrent);
            ps1.setString(5, email);
            ps1.setString(6, pickup);
            ps1.setString(7, dropoff);
            ps1.setString(8, licenseno);
           ps1.executeUpdate();
           
         //code to delete from database
           Statement stmt = (Statement) con.createStatement();
           String sql = "DELETE FROM reservation " +"WHERE resid ="+resid;
                stmt.executeUpdate(sql);
            
	      }
	      catch(Exception e)
	      {
	         e.printStackTrace();
	      }
	     //out.print(resid1+" "+cid1+" "+totalrent);
	     
	     
	     //code to send email
		final String username = "admi.car.rent@gmail.com";//your email id
		final String password = "oslmblnepuwlhdph";// your password
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
	       Session session1 = Session.getInstance(props,
	               new javax.mail.Authenticator() {
	                   @Override
	                   protected PasswordAuthentication getPasswordAuthentication() {
	                       return new PasswordAuthentication(username, password);
	                   }
	               });
	       try {
	           Message message = new MimeMessage(session1);
	         
			message.setFrom(new InternetAddress(email));
	           message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
	           MimeBodyPart textPart = new MimeBodyPart();
	           Multipart multipart = new MimeMultipart();
	           String subject="Final Receipt from Rent motors";
			
String final_Text="Hi "+name+", Greetings from Rent motors, "
		+ "Your Reservation id is " +resid1+ "; "
				+ "Car id:" +cid1+ "; "
						+ "Total rent: " +totalrent+ "; "
								+ " Pick-up place: " +pickup+ "; "
										+ "Destination: " +dropoff+ "; We cleared your credit card credentials from us. Thank you for having bussiness with us."
												+ " Regards,"
		+ " Rent Motors Web team";
	           textPart.setText(final_Text);
	           message.setSubject(subject);
	           multipart.addBodyPart(textPart);
	           message.setContent(multipart);
	           message.setSubject("Final Receipt from Rent motors");
	           //out.println("Sending");
	           Transport.send(message);
	           boolean bln = true;
	           if(bln) {
	        		  out.println("<br><b><p>Your full reservation detail is sent to your mail and bank credentials will be deleted from us. Thank you for having bussiness with us.</p></b>");
	                  RequestDispatcher rd=request.getRequestDispatcher("admin.html");
	                  rd.include(request,response);
	           }
	       } catch (Exception e) {
	           System.out.println(e);
	       }
	       
		
	     
	}

}
