package handler.ecpay;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import Config.EcpayConfig;
import handler.ecpay.payment.domain.ATMRequestObj;
import handler.ecpay.payment.domain.AioCheckOutALL;
import handler.ecpay.payment.domain.AioCheckOutATM;
import handler.ecpay.payment.domain.AioCheckOutBARCODE;
import handler.ecpay.payment.domain.AioCheckOutCVS;
import handler.ecpay.payment.domain.AioCheckOutDevide;
import handler.ecpay.payment.domain.AioCheckOutOneTime;
import handler.ecpay.payment.domain.AioCheckOutPeriod;
import handler.ecpay.payment.domain.AioCheckOutWebATM;
import handler.ecpay.payment.domain.CVSOrBARCODERequestObj;
import handler.ecpay.payment.domain.CaptureObj;
import handler.ecpay.payment.domain.CreateServerOrderObj;
import handler.ecpay.payment.domain.DoActionObj;
import handler.ecpay.payment.domain.FundingReconDetailObj;
import handler.ecpay.payment.domain.InvoiceObj;
import handler.ecpay.payment.domain.QueryCreditCardPeriodInfoObj;
import handler.ecpay.payment.domain.QueryTradeInfoObj;
import handler.ecpay.payment.domain.QueryTradeObj;
import handler.ecpay.payment.domain.TradeNoAioObj;
import handler.ecpay.payment.ecpayOperator.EcpayFunction;
import handler.ecpay.payment.exception.EcpayException;
import handler.ecpay.payment.verification.VerifyCapture;
import handler.ecpay.payment.verification.VerifyCreateServerOrder;
import handler.ecpay.payment.verification.VerifyDoAction;
import handler.ecpay.payment.verification.VerifyFundingReconDetail;
import handler.ecpay.payment.verification.VerifyQueryCreditTrade;
import handler.ecpay.payment.verification.VerifyQueryTrade;
import handler.ecpay.payment.verification.VerifyQueryTradeInfo;
import handler.ecpay.payment.verification.VerifyTradeNoAio;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;

public class AllInOne {

    protected static String operatingMode;
    protected static String mercProfile;
    protected static String isProjectContractor;
    protected static String PlatformID;
    protected static String aioCheckOutUrl;
    protected static String doActionUrl;
    protected static String queryCreditTradeUrl;
    protected static String queryTradeInfoUrl;
    protected static String captureUrl;
    protected static String queryTradeUrl;
    protected static String tradeNoAioUrl;
    protected static String fundingReconDetailUrl;
    protected static String createServerOrderUrl;
    protected static Document verifyDoc;
    protected static String[] ignorePayment;

    public static boolean compareCheckMacValue(Hashtable<String, String> params) {
        String checkMacValue = "";
        if (!params.containsKey("CheckMacValue")) {
            throw new EcpayException("此Hashtable並沒有CheckMacValue可比較");
        } else {
            try {
                checkMacValue = EcpayFunction.genCheckMacValue(EcpayConfig.getHashKey(), EcpayConfig.getHashIV(), params);
            } catch (EcpayException var3) {
                throw new EcpayException("產生檢查碼失敗");
            }

            return checkMacValue.equals(params.get("CheckMacValue"));
        }
    }

    public String createServerOrder(CreateServerOrderObj obj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        obj.setPlatformID(PlatformID);
        if (!PlatformID.isEmpty() && obj.getMerchantID().isEmpty()) {
            obj.setMerchantID(MerchantID);
        } else if (PlatformID.isEmpty() || obj.getMerchantID().isEmpty()) {
            obj.setMerchantID(MerchantID);
        }

        String result = "";
        String CheckMacValue = "";

        try {
            obj.setPaymentToken(EcpayFunction.AESEncode(HashKey, HashIV, obj.getPaymentToken()));
            VerifyCreateServerOrder verify = new VerifyCreateServerOrder();
            createServerOrderUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(obj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) obj);
            String httpValue = EcpayFunction.genHttpValue(obj, CheckMacValue);
            result = EcpayFunction.httpPost(createServerOrderUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        } catch (Exception ex) {
            throw new EcpayException(ex.getMessage());
        }
    }

    public String capture(CaptureObj captureObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        captureObj.setPlatformID(PlatformID);
        if (!PlatformID.isEmpty() && captureObj.getMerchantID().isEmpty()) {
            captureObj.setMerchantID(MerchantID);
        } else if (PlatformID.isEmpty() || captureObj.getMerchantID().isEmpty()) {
            captureObj.setMerchantID(MerchantID);
        }

        String result = "";
        String CheckMacValue = "";

        try {
            VerifyCapture verify = new VerifyCapture();
            captureUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(captureObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) captureObj);
            String httpValue = EcpayFunction.genHttpValue(captureObj, CheckMacValue);
            result = EcpayFunction.httpPost(captureUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String fundingReconDetail(FundingReconDetailObj fundingReconDetailObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        fundingReconDetailObj.setMerchantID(MerchantID);
        String result = "";
        String CheckMacValue = "";

        try {
            VerifyFundingReconDetail verify = new VerifyFundingReconDetail();
            fundingReconDetailUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(fundingReconDetailObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) fundingReconDetailObj);
            String httpValue = EcpayFunction.genHttpValue(fundingReconDetailObj, CheckMacValue);
            result = EcpayFunction.httpPost(fundingReconDetailUrl, httpValue, "BIG5");
            List<String> subRE = new ArrayList();
            Pattern pattern = Pattern.compile("\\d{8}\\,\\d{6}\\,\\d{5}");
            Matcher matcher = pattern.matcher(result);

            while (matcher.find()) {
                subRE.add(matcher.group());
            }

            pattern = Pattern.compile("\\,+\\u6bcf\\u65e5\\u5c0f\\u8a08"); // "每日小計"
            matcher = pattern.matcher(result);
            if (matcher.find()) {
                subRE.add(matcher.group());
            }

            pattern = Pattern.compile("\\,+\\u5408\\u8a08");
            matcher = pattern.matcher(result);
            if (matcher.find()) {
                subRE.add(matcher.group());
            }

            pattern = Pattern.compile("\\u6388\\u6b0a\\u55ae\\u865f"); // "授權單號"
            matcher = pattern.matcher(result);
            if (matcher.find()) {
                subRE.add(matcher.group());
            }

            String tmp;
            for (Iterator var9 = subRE.iterator(); var9.hasNext(); result = result.replace(tmp, "\r\n" + tmp)) {
                tmp = (String) var9.next();
            }

            result = result.substring(2);
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String queryTrade(QueryTradeObj queryTradeObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        queryTradeObj.setMerchantID(MerchantID);
        String result = "";
        String CheckMacValue = "";

        try {
            VerifyQueryTrade verify = new VerifyQueryTrade();
            queryTradeUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(queryTradeObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) queryTradeObj);
            String httpValue = EcpayFunction.genHttpValue(queryTradeObj, CheckMacValue);
            result = EcpayFunction.httpPost(queryTradeUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String tradeNoAio(TradeNoAioObj tradeNoAioObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        tradeNoAioObj.setMerchantID(MerchantID);
        String result = "";
        String CheckMacValue = "";

        try {
            VerifyTradeNoAio verify = new VerifyTradeNoAio();
            tradeNoAioUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(tradeNoAioObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) tradeNoAioObj);
            String httpValue = EcpayFunction.genHttpValue(tradeNoAioObj, CheckMacValue);
            result = EcpayFunction.httpPost(tradeNoAioUrl, httpValue, "BIG5");
            List<String> subRE = new ArrayList();
            Pattern pattern;
            Matcher matcher;
            Iterator var9;
            String tmp;
            if (tradeNoAioObj.getMediaFormated().equals("0")) {
                pattern = Pattern.compile("\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{16}");
                matcher = pattern.matcher(result);

                while (matcher.find()) {
                    subRE.add(matcher.group());
                }

                for (var9 = subRE.iterator(); var9.hasNext(); result = result.replace(tmp, "\r\n" + tmp)) {
                    tmp = (String) var9.next();
                }
            } else if (tradeNoAioObj.getMediaFormated().equals("1")) {
                result = result.replace("=", "");
                pattern = Pattern.compile("\"\\d{4}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2}\"");
                matcher = pattern.matcher(result);

                while (matcher.find()) {
                    subRE.add(matcher.group());
                }

                for (var9 = subRE.iterator(); var9.hasNext(); result = result.replace(tmp, "\r\n" + tmp)) {
                    tmp = (String) var9.next();
                }
            }

            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String doAction(DoActionObj doActionObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        doActionObj.setPlatformID(PlatformID);
        if (!PlatformID.isEmpty() && doActionObj.getMerchantID().isEmpty()) {
            doActionObj.setMerchantID(MerchantID);
        } else if (PlatformID.isEmpty() || doActionObj.getMerchantID().isEmpty()) {
            doActionObj.setMerchantID(MerchantID);
        }

        String result = "";
        String CheckMacValue = "";

        try {
            VerifyDoAction verify = new VerifyDoAction();
            doActionUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(doActionObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) doActionObj);
            String httpValue = EcpayFunction.genHttpValue(doActionObj, CheckMacValue);
            result = EcpayFunction.httpPost(doActionUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String queryTradeInfo(QueryTradeInfoObj queryTradeInfoObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        queryTradeInfoObj.setPlatformID(PlatformID);
        if (!PlatformID.isEmpty() && queryTradeInfoObj.getMerchantID().isEmpty()) {
            queryTradeInfoObj.setMerchantID(MerchantID);
        } else if (PlatformID.isEmpty() || queryTradeInfoObj.getMerchantID().isEmpty()) {
            queryTradeInfoObj.setMerchantID(MerchantID);
        }

        queryTradeInfoObj.setTimeStamp(EcpayFunction.genUnixTimeStamp());
        String result = "";
        String CheckMacValue = "";

        try {
            VerifyQueryTradeInfo verify = new VerifyQueryTradeInfo();
            queryTradeInfoUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(queryTradeInfoObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) queryTradeInfoObj);
            String httpValue = EcpayFunction.genHttpValue(queryTradeInfoObj, CheckMacValue);
            result = EcpayFunction.httpPost(queryTradeInfoUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public String queryCreditCardPeriodInfo(QueryCreditCardPeriodInfoObj queryCreditCardPeriodInfoObj) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        queryCreditCardPeriodInfoObj.setMerchantID(MerchantID);
        queryCreditCardPeriodInfoObj.setTimeStamp(EcpayFunction.genUnixTimeStamp());
        String result = "";
        String CheckMacValue = "";

        try {
            VerifyQueryCreditTrade verify = new VerifyQueryCreditTrade();
            queryCreditTradeUrl = verify.getAPIUrl(operatingMode);
            verify.verifyParams(queryCreditCardPeriodInfoObj);
            CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) queryCreditCardPeriodInfoObj);
            String httpValue = EcpayFunction.genHttpValue(queryCreditCardPeriodInfoObj, CheckMacValue);
            result = EcpayFunction.httpPost(queryCreditTradeUrl, httpValue, "UTF-8");
            return result;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public static EcpayFunction.PaymentInfo aioCheckOut(Object obj, InvoiceObj invoice) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        String ignoreParam = "";
        EcpayFunction.PaymentInfo pay = null;
        String MERCHANTID = MerchantID;
        if (obj instanceof AioCheckOutALL) {
            ((AioCheckOutALL) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutALL) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutALL) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutALL) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutALL) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutALL) obj).setInvoiceMark(invoice == null ? "N" : "Y");
            if (ignorePayment.length > 0) {
                ignoreParam = Arrays.toString(ignorePayment);
                ignoreParam = ignoreParam.replaceAll(", ", "#");
                ignoreParam = ignoreParam.substring(1, ignoreParam.length() - 1);
                ((AioCheckOutALL) obj).setIgnorePayment(ignoreParam);
            }

        } else if (obj instanceof AioCheckOutATM) {
            ((AioCheckOutATM) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutATM) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutATM) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutATM) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutBARCODE) {
            ((AioCheckOutBARCODE) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutBARCODE) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutBARCODE) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutBARCODE) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutBARCODE) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutBARCODE) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutCVS) {
            ((AioCheckOutCVS) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutCVS) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutCVS) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutCVS) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutCVS) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutCVS) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutDevide) {
            ((AioCheckOutDevide) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutDevide) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutDevide) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutDevide) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutDevide) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutDevide) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutOneTime) {
            ((AioCheckOutOneTime) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutOneTime) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutOneTime) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutOneTime) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutOneTime) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutOneTime) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutPeriod) {
            ((AioCheckOutPeriod) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutPeriod) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutPeriod) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutPeriod) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutPeriod) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutPeriod) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else {
            if (!(obj instanceof AioCheckOutWebATM)) {
                throw new EcpayException("傳入非定義的物件導致錯誤!");
            }

            ((AioCheckOutWebATM) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutWebATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutWebATM) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutWebATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutWebATM) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutWebATM) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        }

        try {
            if (invoice != null) {
                invoice.setCustomerName(EcpayFunction.urlEncode(invoice.getCustomerName()));
                invoice.setCustomerAddr(EcpayFunction.urlEncode(invoice.getCustomerAddr()));
                invoice.setCustomerEmail(EcpayFunction.urlEncode(invoice.getCustomerEmail()));
                invoice.setInvoiceItemName(EcpayFunction.urlEncode(invoice.getInvoiceItemName()));
                invoice.setInvoiceItemWord(EcpayFunction.urlEncode(invoice.getInvoiceItemWord()));
                invoice.setInvoiceRemark(EcpayFunction.urlEncode(invoice.getInvoiceRemark()));
            }
            pay = genCheckOutHtmlParameter(obj, invoice);
            return pay;
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }
    }

    public static String aioCheckOutHtml(Object obj, InvoiceObj invoice) {
        final String MerchantID = String.valueOf(EcpayConfig.getMerchantId());
        String ignoreParam = "";
        EcpayFunction.PaymentInfo pay = null;
        String MERCHANTID = MerchantID;
        if (obj instanceof AioCheckOutALL) {
            ((AioCheckOutALL) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutALL) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutALL) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutALL) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutALL) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutALL) obj).setInvoiceMark(invoice == null ? "N" : "Y");
            if (ignorePayment.length > 0) {
                ignoreParam = Arrays.toString(ignorePayment);
                ignoreParam = ignoreParam.replaceAll(", ", "#");
                ignoreParam = ignoreParam.substring(1, ignoreParam.length() - 1);
                ((AioCheckOutALL) obj).setIgnorePayment(ignoreParam);
            }

        } else if (obj instanceof AioCheckOutATM) {
            ((AioCheckOutATM) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutATM) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutATM) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutATM) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutBARCODE) {
            ((AioCheckOutBARCODE) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutBARCODE) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutBARCODE) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutBARCODE) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutBARCODE) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutBARCODE) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutCVS) {
            ((AioCheckOutCVS) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutCVS) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutCVS) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutCVS) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutCVS) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutCVS) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutDevide) {
            ((AioCheckOutDevide) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutDevide) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutDevide) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutDevide) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutDevide) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutDevide) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutOneTime) {
            ((AioCheckOutOneTime) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutOneTime) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutOneTime) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutOneTime) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutOneTime) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutOneTime) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else if (obj instanceof AioCheckOutPeriod) {
            ((AioCheckOutPeriod) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutPeriod) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutPeriod) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutPeriod) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutPeriod) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutPeriod) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        } else {
            if (!(obj instanceof AioCheckOutWebATM)) {
                throw new EcpayException("傳入非定義的物件導致錯誤!");
            }

            ((AioCheckOutWebATM) obj).setPlatformID(PlatformID);
            if (!PlatformID.isEmpty() && ((AioCheckOutWebATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutWebATM) obj).setMerchantID(MERCHANTID);
            } else if (PlatformID.isEmpty() || ((AioCheckOutWebATM) obj).getMerchantID().isEmpty()) {
                ((AioCheckOutWebATM) obj).setMerchantID(MERCHANTID);
            }

            ((AioCheckOutWebATM) obj).setInvoiceMark(invoice == null ? "N" : "Y");
        }

        try {
            if (invoice != null) {
                invoice.setCustomerName(EcpayFunction.urlEncode(invoice.getCustomerName()));
                invoice.setCustomerAddr(EcpayFunction.urlEncode(invoice.getCustomerAddr()));
                invoice.setCustomerEmail(EcpayFunction.urlEncode(invoice.getCustomerEmail()));
                invoice.setInvoiceItemName(EcpayFunction.urlEncode(invoice.getInvoiceItemName()));
                invoice.setInvoiceItemWord(EcpayFunction.urlEncode(invoice.getInvoiceItemWord()));
                invoice.setInvoiceRemark(EcpayFunction.urlEncode(invoice.getInvoiceRemark()));
            }
        } catch (EcpayException ex) {
            ex.ShowExceptionMessage();
            throw new EcpayException(ex.getNewExceptionMessage());
        }

        return genCheckOutHtmlCode(obj, invoice);
    }

    public Object aioCheckOutFeedback(HttpServletRequest req) {
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        List<String> parameterNames = new ArrayList(req.getParameterMap().keySet());
        Iterator var4;
        String name;
        Method method;
        String checkMacValue;
        if (parameterNames.contains("BankCode")) {
            ATMRequestObj obj = new ATMRequestObj();
            var4 = parameterNames.iterator();

            while (var4.hasNext()) {
                name = (String) var4.next();

                try {
                    method = obj.getClass().getMethod("set" + name, (Class[]) null);
                    method.invoke(obj, req.getParameter(name));
                } catch (Exception var8) {
                    throw new EcpayException("物件缺少屬性");
                }
            }
            checkMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) obj);
            if (!checkMacValue.equals(obj.getCheckMacValue())) {
                throw new EcpayException("檢查碼驗證錯誤!");
            } else {
                return obj;
            }
        } else {
            CVSOrBARCODERequestObj obj = new CVSOrBARCODERequestObj();
            var4 = parameterNames.iterator();

            while (var4.hasNext()) {
                name = (String) var4.next();

                try {
                    method = obj.getClass().getMethod("set" + name, (Class[]) null);
                    method.invoke(obj, req.getParameter(name));
                } catch (Exception var9) {
                    throw new EcpayException("物件缺少屬性");
                }
            }

            checkMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, (Object) obj);
            if (!checkMacValue.equals(obj.getCheckMacValue())) {
                throw new EcpayException("檢查碼驗證錯誤!");
            } else {
                return obj;
            }
        }
    }

    private static String genCheckOutHtmlCode(Object aio, InvoiceObj invoice) {
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        StringBuilder builder = new StringBuilder();
        Hashtable<String, String> fieldValue = EcpayFunction.objToHashtable(aio);
        if (invoice != null) {
            Hashtable<String, String> invoiceField = EcpayFunction.objToHashtable(invoice);
            fieldValue.putAll(invoiceField);
        }

        String CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, fieldValue);
        fieldValue.put("CheckMacValue", CheckMacValue);
        Set<String> key = fieldValue.keySet();
        String[] name = (String[]) key.toArray(new String[key.size()]);
        builder.append("<form id=\"allPayAPIForm\" action=\"https://payment.ecpay.com.tw/Cashier/AioCheckOut/V5\" method=\"post\">");

        for (int i = 0; i < name.length; ++i) {
            builder.append("<input type=\"hidden\" name=\"").append(name[i]).append("\" value=\"").append((String) fieldValue.get(name[i])).append("\">");
        }

        builder.append("<script language=\"JavaScript\">");
        builder.append("allPayAPIForm.submit()");
        builder.append("</script>");
        builder.append("</form>");
        String s = builder.toString();
        return s;
    }

    private static EcpayFunction.PaymentInfo genCheckOutHtmlParameter(Object aio, InvoiceObj invoice) {
        final String HashKey = EcpayConfig.getHashKey();
        final String HashIV = EcpayConfig.getHashIV();
        Hashtable<String, String> fieldValue = EcpayFunction.objToHashtable(aio);
        if (invoice != null) {
            Hashtable<String, String> invoiceField = EcpayFunction.objToHashtable(invoice);
            fieldValue.putAll(invoiceField);
        }

        String CheckMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, fieldValue);
        fieldValue.put("CheckMacValue", CheckMacValue);
        Set<String> key = fieldValue.keySet();
        String[] name = (String[]) key.toArray(new String[key.size()]);
        ArrayList<NameValuePair> val = new ArrayList();

        for (int i = 0; i < name.length; ++i) {
            val.add(new NameValuePair(name[i], (String) fieldValue.get(name[i])));
        }

        EcpayFunction.PaymentInfo payment = null;

        try {
            payment = EcpayFunction.htmlunit("https://payment.ecpay.com.tw/Cashier/AioCheckOut/V5", val, "UTF-8", aio);
        } catch (Exception ex) {
            Logger.getLogger(AllInOne.class.getName()).log(Level.SEVERE, (String) null, ex);
        }

        return payment;
    }

    static {
        PlatformID = "";
    }
}
