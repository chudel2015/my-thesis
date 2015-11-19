/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class InstallYarn {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\CH\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    public static void main(String[] args) throws JSchException, IOException, InterruptedException {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\CH\\.aws\\credentials).
         */
    	
    	
        AWSCredentials credentials = null;
        try {
            credentials = new BasicAWSCredentials("Your Access Key ID", "Your Secret Access Key");
            
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\CH\\.aws\\credentials), and is in valid format.",
                    e);
        }

        // Create the AmazonEC2Client object so we can call various APIs.
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        ec2.setRegion(usWest2);

        /*
        // Create a new security group.
        try {
            CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(
                    "GettingStartedGroup", "Getting Started Security Group");
            CreateSecurityGroupResult result = ec2
                    .createSecurityGroup(securityGroupRequest);
            System.out.println(String.format("Security group created: [%s]",
                    result.getGroupId()));
        } catch (AmazonServiceException ase) {
            // Likely this means that the group is already created, so ignore.
            System.out.println(ase.getMessage());
        }

        String ipAddr = "0.0.0.0/0";

        // Get the IP of the current host, so that we can limit the Security Group
        // by default to the ip range associated with your subnet.
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getHostAddress()+"/10";
        } catch (UnknownHostException e) {
        }

        // Create a range that you would like to populate.
        List<String> ipRanges = Collections.singletonList(ipAddr);

        // Open up port 23 for TCP traffic to the associated IP from above (e.g. ssh traffic).
        IpPermission ipPermission = new IpPermission()
                .withIpProtocol("tcp")
                .withFromPort(new Integer(22))
                .withToPort(new Integer(22))
                .withIpRanges(ipRanges);

        List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);

        try {
            // Authorize the ports to the used.
            AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
                    "GettingStartedGroup", ipPermissions);
            ec2.authorizeSecurityGroupIngress(ingressRequest);
            System.out.println(String.format("Ingress port authroized: [%s]",
                    ipPermissions.toString()));
        } catch (AmazonServiceException ase) {
            // Ignore because this likely means the zone has already been authorized.
            System.out.println(ase.getMessage());
        }
        */
    	//CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest()
		//.withKeyName("CHENHAO");
		
		//CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);
        
		//KeyPair keyPair = new KeyPair();
		//keyPair = createKeyPairResult.getKeyPair();
		//String privateKey = keyPair.getKeyMaterial();
        
		
		int cluster_size =Integer.parseInt(JOptionPane.showInputDialog("Enter number of machines you want to set"));
		String ami_name = JOptionPane.showInputDialog("Enter your ami name");
		String key_name = JOptionPane.showInputDialog("Enter your key name");
		String s_group_name = JOptionPane.showInputDialog("Enter your security group name");
		
    RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
    runInstancesRequest.withImageId(ami_name)
			               .withInstanceType("t2.micro")
			               .withMinCount(cluster_size)
			               .withMaxCount(cluster_size)
			               .withKeyName(key_name)
			               .withSecurityGroups(s_group_name);
    
		    RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		
        Thread.sleep(10000);

		    Vector<String> instanceId = new Vector<String>();
		    for(Instance ins : runInstancesResult.getReservation().getInstances())
		      instanceId.add(ins.getInstanceId());
		        
    		DescribeInstancesRequest request =  new DescribeInstancesRequest();
        request.setInstanceIds(instanceId);
        DescribeInstancesResult result = ec2.describeInstances(request);
        List<Reservation> reservations = result.getReservations();        
        
        List<Instance> instances_list = new Vector<Instance>();
        for(int i=0; i<reservations.size(); i++)
        	instances_list.addAll(reservations.get(i).getInstances());
                
        
        System.out.println("Plan cluster size:"+cluster_size+" Real size:"+instances_list.size());
        
		      JSch jsch=new JSch(); 
		      JFileChooser chooser = new JFileChooser();
		      chooser.setDialogTitle("Choose your privatekey");
		      chooser.setFileHidingEnabled(false);
		      int returnVal = chooser.showOpenDialog(null);
		      if(returnVal == JFileChooser.APPROVE_OPTION) {
		        System.out.println("You chose "+
					   chooser.getSelectedFile().getAbsolutePath()+".");
		        jsch.addIdentity(chooser.getSelectedFile().getAbsolutePath()
					 );
		      }
		

		      Session session;
		      UserInfo ui=new MyUserInfo();
		      for(int i=0; i<instances_list.size(); i++)
		      {
		    	  if(instances_list.get(i).getPublicIpAddress()==null)
		      		  System.out.println("Error, public ip is null\n");
		             
		    	  System.out.println("Connect to:"+instances_list.get(i).getPublicIpAddress()+"\n");
		    	  session = jsch.getSession("ubuntu", instances_list.get(i).getPublicIpAddress(), 22);
		    	  session.setUserInfo(ui);
		    	  session.connect();
		    	  
		    	  //
		    	  //if(i==0)
		    	  //{
		          //  transfer_file_to("/home/ubuntu","C:/Users/CH/Downloads/ch.pem",session);
		           // exec("chmod 400 /home/ubuntu/ch.pem",session);
		    	  //}
		    	  
		    	  
		    	  //slaves file
		    	  for(int j=0; j<instances_list.size(); j++)
		    	  {
		    		  if(j!=0)
		    			  exec("echo "+instances_list.get(j).getPrivateIpAddress()+
		    			  "\n >> /usr/local/hadoop/etc/hadoop/slaves",session);
		    	  }
		    	  //core-site file
            String command = "sed -i 's#Master#"+instances_list.get(0).getPrivateIpAddress()+
            "#g' /usr/local/hadoop/etc/hadoop/core-site.xml";
		    	  exec(command,session);
		    	  
	          //hdfs-size file
            command = "sed -i 's#Master#"+instances_list.get(0).getPrivateIpAddress()+
            "#g' /usr/local/hadoop/etc/hadoop/core-site.xml";
		    	  exec(command,session);
		    	  
            command = "sed -i 's#replication#"+Integer.toString(cluster_size-1)+
            "#g' /usr/local/hadoop/etc/hadoop/core-site.xml";
            exec(command,session);
            
	          //yarn-size file
            command = "sed -i 's#Master#"+instances_list.get(0).getPrivateIpAddress()+
            "#g' /usr/local/hadoop/etc/hadoop/core-site.xml";
		    	  exec(command,session);
		    	  
	              session.disconnect();
		      }
		      
		      //username and passphrase will be given via UserInfo interface.
		      
		      //slaves file
             
             

    }
    
    static void transfer_file_to(String rfile, String lfile, Session session) throws JSchException, IOException{
  	  
        // username and password will be given via UserInfo interface.
  	  FileInputStream fis=null;
        boolean ptimestamp = false;
   
        // exec 'scp -t rfile' remotely
        String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);
   
        // get I/O streams for remote scp
        OutputStream out=channel.getOutputStream();
        InputStream in=channel.getInputStream();
   
        channel.connect();
   
        if(checkAck(in)!=0){
      	  System.exit(0);
        }
   
        File _lfile = new File(lfile);
   
        if(ptimestamp){
          command="T"+(_lfile.lastModified()/1000)+" 0";
          // The access time should be sent here,
          // but it is not accessible with JavaAPI ;-<
          command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
          out.write(command.getBytes()); out.flush();
          if(checkAck(in)!=0){
    	  System.exit(0);
          }
        }
   
        // send "C0644 filesize filename", where filename should not include '/'
        long filesize=_lfile.length();
        command="C0644 "+filesize+" ";
        if(lfile.lastIndexOf('/')>0){
          command+=lfile.substring(lfile.lastIndexOf('/')+1);
        }
        else{
          command+=lfile;
        }
        command+="\n";
        out.write(command.getBytes()); out.flush();
        if(checkAck(in)!=0){
  	System.exit(0);
        }
   
        // send a content of lfile
        fis=new FileInputStream(lfile);
        byte[] buf=new byte[1024];
        while(true){
          int len=fis.read(buf, 0, buf.length);
  	if(len<=0) break;
          out.write(buf, 0, len); //out.flush();
        }
        fis.close();
        fis=null;
        // send '\0'
        buf[0]=0; out.write(buf, 0, 1); out.flush();
        if(checkAck(in)!=0){
  	System.exit(0);
        }
        out.close();
        channel.disconnect();
    }
   
  public static void Replace(String file_s, String file_tmp_s, String searchText, String replaceText) throws IOException {
	  
	    File file = new File(file_s);
	    File file_tmp = new File(file_tmp_s);
	    FileReader fis = new FileReader(file);
  		char[] data = new char[1024];
          int rn = 0;
          StringBuilder sb = new StringBuilder();
          while ((rn = fis.read(data)) > 0) {
              String str = String.valueOf(data, 0, rn);
              sb.append(str);
          }
          fis.close();
          String str = sb.toString().replace(searchText, replaceText);
          FileWriter fout = new FileWriter(file_tmp);
          fout.write(str.toCharArray());
          fout.close();
  		
  	}

   static int checkAck(InputStream in) throws IOException{
  	    int b=in.read();
  	    // b may be 0 for success,
  	    //          1 for error,
  	    //          2 for fatal error,
  	    //          -1
  	    if(b==0) return b;
  	    if(b==-1) return b;
  	 
  	    if(b==1 || b==2){
  	      StringBuffer sb=new StringBuffer();
  	      int c;
  	      do {
  		c=in.read();
  		sb.append((char)c);
  	      }
  	      while(c!='\n');
  	      if(b==1){ // error
  		System.out.print(sb.toString());
  	      }
  	      if(b==2){ // fatal error
  		System.out.print(sb.toString());
  	      }
  	    }
  	    return b;
  	  }
   
   public static void exec(String command, Session session) throws JSchException, IOException{
  	 
  	 Channel channel=session.openChannel("exec");
  	 ((ChannelExec)channel).setCommand(command);

  	 channel.setInputStream(null);
  	 ((ChannelExec)channel).setErrStream(System.err);

  	 InputStream in=channel.getInputStream();

  	 channel.connect();

  	 byte[] tmp=new byte[1024];
  	 while(true){
  		 while(in.available()>0){
  			 int i=in.read(tmp, 0, 1024);
  	if(i<0)break;
  	System.out.print(new String(tmp, 0, i));
  	}
  	if(channel.isClosed()){
  	if(in.available()>0) continue; 
  	System.out.println("exit-status: "+channel.getExitStatus());
  	break;
  	}
  	try{Thread.sleep(1000);}catch(Exception ee){}
  	}
  	channel.disconnect();
  	 
  }
   
   
    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
      public String getPassword(){ return null; }
      public boolean promptYesNo(String str){
        Object[] options={ "yes", "no" };
        int foo=JOptionPane.showOptionDialog(null, 
               str,
               "Warning", 
               JOptionPane.DEFAULT_OPTION, 
               JOptionPane.WARNING_MESSAGE,
               null, options, options[0]);
         return foo==0;
      }
    
      String passphrase;
      JTextField passphraseField=(JTextField)new JPasswordField(20);
   
      public String getPassphrase(){ return passphrase; }
      public boolean promptPassphrase(String message){
        Object[] ob={passphraseField};
        int result=
  	JOptionPane.showConfirmDialog(null, ob, message,
  				      JOptionPane.OK_CANCEL_OPTION);
        if(result==JOptionPane.OK_OPTION){
          passphrase=passphraseField.getText();
          return true;
        }
        else{ return false; }
      }
      public boolean promptPassword(String message){ return true; }
      public void showMessage(String message){
        JOptionPane.showMessageDialog(null, message);
      }
      final GridBagConstraints gbc = 
        new GridBagConstraints(0,0,1,1,1,1,
                               GridBagConstraints.NORTHWEST,
                               GridBagConstraints.NONE,
                               new Insets(0,0,0,0),0,0);
      private Container panel;
      public String[] promptKeyboardInteractive(String destination,
                                                String name,
                                                String instruction,
                                                String[] prompt,
                                                boolean[] echo){
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
   
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        panel.add(new JLabel(instruction), gbc);
        gbc.gridy++;
   
        gbc.gridwidth = GridBagConstraints.RELATIVE;
   
        JTextField[] texts=new JTextField[prompt.length];
        for(int i=0; i<prompt.length; i++){
          gbc.fill = GridBagConstraints.NONE;
          gbc.gridx = 0;
          gbc.weightx = 1;
          panel.add(new JLabel(prompt[i]),gbc);
   
          gbc.gridx = 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          gbc.weighty = 1;
          if(echo[i]){
            texts[i]=new JTextField(20);
          }
          else{
            texts[i]=new JPasswordField(20);
          }
          panel.add(texts[i], gbc);
          gbc.gridy++;
        }
   
        if(JOptionPane.showConfirmDialog(null, panel, 
                                         destination+": "+name,
                                         JOptionPane.OK_CANCEL_OPTION,
                                         JOptionPane.QUESTION_MESSAGE)
           ==JOptionPane.OK_OPTION){
          String[] response=new String[prompt.length];
          for(int i=0; i<prompt.length; i++){
            response[i]=texts[i].getText();
          }
  	return response;
        }
        else{
          return null;  // cancel
        }
      }
    }
    	
}
