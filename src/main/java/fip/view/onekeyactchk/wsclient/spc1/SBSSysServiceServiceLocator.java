/**
 * SBSSysServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fip.view.onekeyactchk.wsclient.spc1;

public class SBSSysServiceServiceLocator extends org.apache.axis.client.Service implements SBSSysServiceService {

    public SBSSysServiceServiceLocator() {
    }


    public SBSSysServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SBSSysServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SBSSysService
    private java.lang.String SBSSysService_address = "http://10.143.20.144:10002/SupplyChainSysPortal/services/SBSSysService";

    public java.lang.String getSBSSysServiceAddress() {
        return SBSSysService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SBSSysServiceWSDDServiceName = "SBSSysService";

    public java.lang.String getSBSSysServiceWSDDServiceName() {
        return SBSSysServiceWSDDServiceName;
    }

    public void setSBSSysServiceWSDDServiceName(java.lang.String name) {
        SBSSysServiceWSDDServiceName = name;
    }

    public SBSSysService_PortType getSBSSysService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SBSSysService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSBSSysService(endpoint);
    }

    public SBSSysService_PortType getSBSSysService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SBSSysServiceSoapBindingStub _stub = new SBSSysServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getSBSSysServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSBSSysServiceEndpointAddress(java.lang.String address) {
        SBSSysService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SBSSysService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                SBSSysServiceSoapBindingStub _stub = new SBSSysServiceSoapBindingStub(new java.net.URL(SBSSysService_address), this);
                _stub.setPortName(getSBSSysServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SBSSysService".equals(inputPortName)) {
            return getSBSSysService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://10.143.20.144:10002/SupplyChainSysPortal/services/SBSSysService", "SBSSysServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://10.143.20.144:10002/SupplyChainSysPortal/services/SBSSysService", "SBSSysService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

if ("SBSSysService".equals(portName)) {
            setSBSSysServiceEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
