import com.sun.star.lang.*;
import com.sun.star.lib.uno.helper.Factory;
//import com.sun.star.lang.XMultiServiceFactory;
//import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.sheet.XAddIn;
import com.sun.star.uno.*;
import librenumerals.XLibreNumeralsCalc;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.programmisty.numerals.Numerals.*;

/** This outer class provides an inner class to implement the service
 * description, a method to instantiate the
 * component on demand (__getServiceFactory()), and a method to give
 * information about the component (__writeRegistryServiceInfo()).
 */

public class LibreNumeralsCalcService {

    /** This inner class provides the component as a concrete implementation
     * of the service description. It implements the needed interfaces.
     * @implements XNumerals, XAddIn, XServiceName, XServiceInfo, XTypeProvider
     */
    public static class _LibreNumeralsCalcServiceImpl extends WeakBase implements
            XLibreNumeralsCalc,
            XAddIn,
            XServiceName,
            XServiceInfo {

        /** The component will be registered under this name.
         */
        public static final String SERVICE_NAME = "com.upsilon-studio.librenumerals";

        private static final String ADDIN_SERVICE = "com.sun.star.sheet.AddIn";

        private Locale aFuncLoc;

        private HashMap<String, SoloFunction> mFunctionsTable = new HashMap(100);

        private class SoloParameter {
            final String displayName;
            final String description;
            SoloParameter(String displayName, String description) {
                this.displayName = displayName;
                this.description = description;
            }
        }

        private class SoloFunction {
            final String name;
            final String displayName;
            final String description;
            final SoloParameter[] parameters;

            SoloFunction(String name, String displayName, String description, SoloParameter[] parameters) {
                this.name = name;
                this.displayName = displayName;
                this.description = description;
                this.parameters = parameters;
            }

        }

        private SoloFunction fnAmountRU = new SoloFunction(
                "amountRU",
                "AMOUNT.RU",
                "Сумма прописью в рублях в виде строки",
                new SoloParameter[]{new SoloParameter("Сумма","Сумма в виде числа")}
        );

        private SoloFunction fnAmountUS = new SoloFunction(
                "amountUS",
                "AMOUNT.US",
                "USD Amount in words",
                new SoloParameter[]{new SoloParameter("Amount","Amount as a number")}
        );

        private SoloFunction fnAmountUZ = new SoloFunction(
                "amountUZ",
                "AMOUNT.UZ",
                "Amount in Uzbek words",
                new SoloParameter[]{new SoloParameter("Amount","Amount as a number")}
        );

        private void putFunction(SoloFunction fn) {
            mFunctionsTable.put(fn.name, fn);
        }

        private void initFunctionsTable() {
            putFunction(fnAmountRU);
//            putFunction(fnAmountUS);
            putFunction(fnAmountUZ);
        }


        /** TO DO:
         * This is where you implement all methods of your interface. The parameters have to
         * be the same as in your IDL file and their types have to be the correct
         * IDL-to-Java mappings of their types in the IDL file.
         */
        public String amountRU(
                com.sun.star.beans.XPropertySet xOptions,
                double amount
        ) /*throws com.sun.star.uno.Exception*/ {
            return russianRubles(new BigDecimal(amount));
        }

        public String amountUS(
                com.sun.star.beans.XPropertySet xOptions,
                double amount
        ) {
            return amount(new BigDecimal(amount));
        }

        public String amountUZ(
                com.sun.star.beans.XPropertySet xOptions,
                double amount
        ) {
            return uzbekSums(new BigDecimal(amount));
        }

        public _LibreNumeralsCalcServiceImpl(XComponentContext _xContext) throws com.sun.star.uno.Exception {
            initFunctionsTable();
        }

        // Implement method from interface XServiceName
        public String getServiceName() {
            return SERVICE_NAME;
        }

        // Implement methods from interface XServiceInfo
        public boolean supportsService(String stringServiceName) {
            return (stringServiceName.equals(ADDIN_SERVICE) ||
                    stringServiceName.equals(SERVICE_NAME));
        }

        public String getImplementationName() {
            return _LibreNumeralsCalcServiceImpl.class.getName();
        }

        public static String[] getServiceNames() {
            String[] stringSupportedServiceNames = {ADDIN_SERVICE, SERVICE_NAME};
            return stringSupportedServiceNames;
        }

        public String[] getSupportedServiceNames() {
            return getServiceNames();
        }

        // Implement methods from interface XAddIn
        public String getDisplayArgumentName(String functionName, int intArgument) {
            if(intArgument == 0) {
                return "(internal)";
            }
            SoloFunction fn = mFunctionsTable.get(functionName);
            return fn.parameters[intArgument-1].displayName;
        }

        public String getDisplayFunctionName(String functionName) {
            SoloFunction fn = mFunctionsTable.get(functionName);
            return fn.displayName;
        }

        public String getProgrammaticCategoryName(String p1) {
            return "Add-In";
        }

        public String getDisplayCategoryName(String p1) {
            return "LibreNumerals";
        }

        public String getFunctionDescription(String functionName) {
            SoloFunction fn = mFunctionsTable.get(functionName);
            return fn.description;
        }

        public String getArgumentDescription(String functionName, int intArgument) {
            if(intArgument == 0) {
                return "(internal)";
            }
            SoloFunction fn = mFunctionsTable.get(functionName);
            return fn.parameters[intArgument-1].description;
        }

        public String getProgrammaticFuntionName(String p1) {
            return "";
        }

        // Implement methods from interface XLocalizable
        public Locale getLocale() {
            return aFuncLoc;
        }

        public void setLocale(Locale p1) {
            aFuncLoc = p1;
        }

//        public void initialize(Object[] object) throws com.sun.star.uno.Exception {
//            if (object.length > 0) {
//                m_xFrame = UnoRuntime.queryInterface(XFrame.class, object[0]);
//            }
//        }

    }

    /**
     * Returns a factory for creating the service.
     * This method is called by the <code>JavaLoader</code>
     * <p>
     * @return returns a <code>XSingleServiceFactory</code> for creating the component
     * @param   implName     the name of the implementation for which a service is desired
     * @param   multiFactory the service manager to be used if needed
     * @param   regKey       the registryKey
     * @see                  com.sun.star.comp.loader.JavaLoader
     */
/*
    public static XSingleServiceFactory __getServiceFactory(String implName,
    XMultiServiceFactory multiFactory,
    XRegistryKey regKey) {
        XSingleServiceFactory xSingleServiceFactory = null;

log.debug("getServiceFactory: "+implName);

        if (implName.equals(_FishCalcServiceImpl.class.getName()) )
            xSingleServiceFactory = FactoryHelper.getServiceFactory(_FishCalcServiceImpl.class,
            _FishCalcServiceImpl.__serviceName,
            multiFactory,
            regKey);

        return xSingleServiceFactory;
    }
*/

    /**
     * Gives a factory for creating the service.
     * This method is called by the <code>JavaLoader</code>
     * <p>
     * @return returns a <code>XSingleComponentFactory</code> for creating
     *          the component
     * @param   sImplName the name of the implementation for which a
     *          service is desired
     * @see     com.sun.star.comp.loader.JavaLoader
     */
    public static XSingleComponentFactory __getComponentFactory(String sImplName) {

        XSingleComponentFactory xFactory = null;

        if (sImplName.equals(_LibreNumeralsCalcServiceImpl.class.getName()))
            xFactory = Factory.createComponentFactory(_LibreNumeralsCalcServiceImpl.class, _LibreNumeralsCalcServiceImpl.getServiceNames());

        return xFactory;
    }

    /**
     * Writes the service information into the given registry key.
     * This method is called by the <code>JavaLoader</code>
     * <p>
     * @return returns true if the operation succeeded
     * @param   regKey the registryKey
     * @see     com.sun.star.comp.loader.JavaLoader
     */
    public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {
        return
                Factory.writeRegistryServiceInfo(_LibreNumeralsCalcServiceImpl.class.getName(), _LibreNumeralsCalcServiceImpl.getServiceNames(), regKey);

    }

}
