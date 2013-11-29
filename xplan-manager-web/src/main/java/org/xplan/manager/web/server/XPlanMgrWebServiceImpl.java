package org.xplan.manager.web.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.xplan.manager.web.client.XPlanMgrWebService;
import org.xplan.manager.web.shared.Plan;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class XPlanMgrWebServiceImpl extends RemoteServiceServlet implements XPlanMgrWebService {
	
	 
	
    @Override
    protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)
        throws ServletException, IOException
    {
        String id = p_request.getParameter("plan");
        Plan plan=null;
        HttpSession session = p_request.getSession(false);
		if( session != null ){
			ArrayList<Plan> pList = (ArrayList<Plan>) session.getAttribute("plans");
			for( Plan p : pList){
				if( p.getId().equals(id)){
					plan=p;
				}
			}
			
		}
        
        if (plan == null)
        {
            p_response.sendError(666, "Missing filename");
            return;
        }
//        File file = new File(filename);
//        long length = file.length();
        long length = 32768;
//        FileInputStream fis = new FileInputStream(file);

        ServletOutputStream out = p_response.getOutputStream();
        
//        int bufSize = p_response.getBufferSize();
//        byte[] buffer = new byte[bufSize];
//        new Random().nextBytes(buffer);
        //BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
//        int bytes=32768;
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput output = null;
        try {
        	  output = new ObjectOutputStream(bos);   
        	  output.writeObject(plan);
        	  byte b[] = bos.toByteArray();
        	  out.write(b);
        	  length = b.length;
        	} finally {
        		output.close();
        	  bos.close();
        	}
        
        p_response.addHeader("Content-Disposition","attachment; filename=\"" + plan.getName() +".obj\"");
        p_response.setContentType("application/octet-stream");
        if (length > 0 && length <= Integer.MAX_VALUE);
            p_response.setContentLength((int)length);
        p_response.setBufferSize(32768);
        
        
//        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
//            out.write(buffer, 0, bytes);
//        bis.close();
//        fis.close();
        out.flush();
        out.close();
    }

	
	
    @Override
    protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
	boolean isMultiPart = ServletFileUpload.isMultipartContent(new ServletRequestContext(request));
 
	if(isMultiPart) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
 
		try {
			//request.get
			
			List<FileItem> items = upload.parseRequest(request);
			FileItem uploadedFileItem = items.get(0); // we only upload one file
 
			if(uploadedFileItem == null) {
				super.service(request, response);
				return;
			} else if(uploadedFileItem.getFieldName().equalsIgnoreCase("uploadFormElement")) {
				String fileName = uploadedFileItem.getName();
				response.setStatus(HttpServletResponse.SC_CREATED);
				response.getWriter().print("Loaded " + fileName + " with " + uploadedFileItem.getSize() + " Bytes");
				response.flushBuffer();
				
//				response.       
//				String fileName = req.getParameter( "fileInfo1" );
//
//	            int BUFFER = 1024 * 100;
//	            response.setContentType( "application/octet-stream" );
//	            response.setHeader( "Content-Disposition:", "attachment;filename=" + "\"" + fileName + "\"" );
//	            ServletOutputStream outputStream = response.getOutputStream();
//	            response.setContentLength( Long.valueOf( getfile(fileName).length() ).intValue() );
//	            response.setBufferSize( BUFFER );
//	            //Your IO code goes here to create a file and set to outputStream//
				
				HttpSession session = request.getSession(true);
				ArrayList<Plan> plans = (ArrayList<Plan>) session.getAttribute("plans");
				if( plans == null){
					plans = new ArrayList<Plan>();
					session.setAttribute("plans", plans);
				}

				plans.add(new Plan(fileName, ""+uploadedFileItem.getSize(), uploadedFileItem.getContentType()));

			}
 
		} catch(FileUploadException e) {
			//LOG.error(e);
		}
	}
 
	else {
		super.service(request, response);
		return;
	}
    }
	
	@Override
	public List<Plan> getPlans() {
		//input = escapeHtml(input);
		
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		
		//TODO Maybe change to map<plan, id>
		ArrayList<Plan> plans = (ArrayList<Plan>) session.getAttribute("plans");
		if( plans == null){
			plans = new ArrayList<Plan>();
			session.setAttribute("plans", plans);
			for(int i=1;i<=10;i++){
				Plan p = new Plan("plan"+i, Long.toHexString(Double.doubleToLongBits(Math.random())), "Typ"+(int)(Math.random()*10+1));
				p.setLoaded(Math.random()<0.5);
				plans.add(p);
			}


			
		}
		return plans;
	}

	@Override
	public int removePlan(String id) {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if( session != null ){
			ArrayList<Plan> pList = (ArrayList<Plan>) session.getAttribute("plans");
			for( Plan p : pList){
				if( id.equals(p.getId())){
					pList.remove(p);
					return 0;
				}
			}
			
		}
		return 1;
	}

	@Override
	public int validatePlan(String id) {
		Plan p = getPlan(id);
		if( p != null ){
			p.setValidated(true);
			return 0;
		}
		return 1;
	}

	@Override
	public int loadPlan(String id) {
		Plan p = getPlan(id);
		if( p != null ){
			p.setLoaded(true);
			return 0;
		}
		return 1;
	}

	@Override
	public Plan getPlan(String id) {
		HttpServletRequest request = this.getThreadLocalRequest();
		if( request != null){
			HttpSession session = request.getSession(false);
			if( session != null ){
				ArrayList<Plan> pList = (ArrayList<Plan>) session.getAttribute("plans");
				for( Plan p : pList){
					if( p.getId().equals(id)){
						return p;
					}
				}
				
			}
		}
		return null;
	}
	
}
