package com.infy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.infy.validator.Validator;
import com.infy.dao.MobileServiceDAO;
import com.infy.dao.MobileServiceDAOImpl;
import com.infy.exception.MobileServiceException;
import com.infy.model.ServiceReport;
import com.infy.model.ServiceRequest;
import com.infy.model.Status;

public class MobileServiceImpl implements MobileService{
	
	private MobileServiceDAO dao =  new MobileServiceDAOImpl();
    private Validator validator=new Validator();
        
	@Override
	public ServiceRequest registerRequest(ServiceRequest service) throws MobileServiceException {
		ServiceRequest ServiceFromDao;	//object 
		try {
			validator.validate(service);	//invoking validate() from validator class
			Float serviceFee = calculateEstimateCost(service.getIssue());	//getting error
			if(serviceFee<=0){
				throw newMobileServiceException("Sorry, we do not provide that service");	//getting error
			}	//If the Float value returned in the previous step is less than or equal to 0, throw a new MobileServiceException with the message "Sorry, we do not provide that service."
			service.setServiceFee(serviceFee);	//Else, set the value of serviceFee in the ServiceRequest with the Float value.
			service.setStatus(Status.ACCEPTED);	//Set the values of status to ACCEPTED 
			service.setTimeOfRequest(LocalDateTime.now());	//and timeOfRequest to the current date and time value. (Use LocalDateTime).
			ServiceFromDao=dao.registerRequest(service);	//Invoke the registerRequest() of the MobileServiceDAOImpl class by passing the ServiceRequest object as the argument which will return another ServiceRequest object.
			
		}
		catch(MobileServiceException mobileServiceException){
			if(mobileServiceException.getMessage().startsWith("service")) {
				LogFactory.getLog(getClass()).error(mobileServiceException.getMessage());	//getting error
			}
			throw mobileServiceException;
			
		}
		return ServiceFromDao;
	}

	@Override
	public Float calculateEstimateCost(List<String> issues) throws MobileServiceException {
		Float totalCost=0.0F;
		for(String issue: issues) {
			if(issue.equalsIgnoreCase("BATTERY")) {
				totalCost +=10;
			}
			else if(issue.equalsIgnoreCase("CAMERA")){
				totalCost +=5;
			}
			else if(issue.equalsIgnoreCase("SCREEN")) {
				totalCost +=15;
			}
		}
		return totalCost;
	}

	@Override
	public List<ServiceReport> getServices(Status status) throws MobileServiceException {
		List<ServiceReport> ServiceReportList = new ArrayList<>();
		try {
			ServiceReportList=dao.getServices()												//getting error
					.parallelStream()
					.filter(serviceRequest -> serviceRequest.getStatus().equals(status))
					.map(serviceRequest -> new ServiceReport())						
					.collect(Collectors.toList());
			if(ServiceReportList.isEmpty()){
				throw new MobileServiceException("Sorry, we did not find any records for your query");
			}
			
		}
		catch(MobileServiceException mobileServiceException) {
			if(mobileServiceException.getMessage().startsWith("service")) {
				LogFactory.getLog(getClass()).error(mobileServiceException.getMessage());	//getting error
			}
			throw mobileServiceException;
		}
		return ServiceReportList;
	}

}





/*
 * 
 * 

 Query 
 
MobileServiceImpl



Initialize a new instance of MobileServiceDAOImpl  and Validator class for the private attribute.

registerRequest(ServiceRequest service):

This method is used to validate the ServiceRequest object received and add the request to the database based on the request status.
Invoke the validate() of the Validator class by passing the ServiceRequest object as the parameter.
If the ServiceRequest object is valid, invoke the calculateEstimateCost() method bypassing the issues attribute from the ServiceRequest object as the argument which will return a Float value.
If the Float value returned in the previous step is less than or equal to 0, throw a new MobileServiceException with the message "Sorry, we do not provide that service."
Else, set the value of serviceFee in the ServiceRequest with the Float value.
Set the values of status to ACCEPTED and timeOfRequest to the current date and time value. (Use LocalDateTime).
Invoke the registerRequest() of the MobileServiceDAOImpl class by passing the ServiceRequest object as the argument which will return another ServiceRequest object.
Return the ServiceRequest object retrieved in the previous step.
calculateEstimateCost(List<String> issues):

This method is used to calculate the serviceFee from the issues.
Iterate through the list of issues and calculate the total serviceFee based on the issue. (Do case insensitive comparison)


Return the sum of all values.
If none of the issues match, return 0.
getServices(Status status):

This method is used to fetch all the ServiceReport objects from the database which have the required status.
Initialize a new List<ServiceReport> object.
Invoke the getServices() of the MobileServiceDAOImpl class which will return a List<ServiceRequest>.
Filter the List<ServiceRequst> based on the status and populate the List<ServiceReport>.
If the List<ServiceReport> is empty, throw a new MobileServiceException with the message "Sorry, we did not find any records for your query.".
Else, return the List<ServiceReport> object.

*/