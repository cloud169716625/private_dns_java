package com.matthew.privatedns;
import android.os.AsyncTask;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import org.xbill.DNS.dnssec.ValidatingResolver;

import java.io.IOException;

public class MyViewModel extends BaseObservable {
//    public final MutableLiveData<String> domain = new MutableLiveData<>("www.fast.com.\n fiber.google.com.\n www.speedtest.net.\n");
    private String domain;
    public final ObservableField<String> result = new ObservableField<>("");

    @Bindable
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        notifyPropertyChanged(BR.domain);
    }


    public void onResolver() {
        new DnsResolverTask().execute();
    }

    private class DnsResolverTask extends AsyncTask<Void, Void, String> {
        private String ROOT = ". IN DS 20326 8 2 E06D44B80B8F1D39A95C0B0D7C65D08458E880409BBC683457104237C7F8EC8D";
        @Override
        protected String doInBackground(Void... params) {
            String message = "";
            try {
                if (
                    domain.equalsIgnoreCase("fast.com")
                        || domain.equalsIgnoreCase("speedtest.net")
                        || domain.equalsIgnoreCase("fiber.google.com")
                ) {
                    SimpleResolver resolver1 = new SimpleResolver("10.122.14.158");
                    SimpleResolver resolver2 = new SimpleResolver("10.122.14.158");
                    message = "Resolver-1 \n\n";
                    message += sendAndPrint(resolver1, domain + ".") + "\n\n";
                    message = "Resolver-2 \n\n";
                    message += sendAndPrint(resolver2, domain + ".");
                } else {
                    SimpleResolver sr = new SimpleResolver("8.8.4.4");
                    System.out.println("Standard resolver: \n");
                    message += "Resolver \n\n";
                    message = sendAndPrint(sr, domain + ".");
                }
            } catch (Exception e) {
                Log.e("Error------------>", e.toString());
                handleGenericError(e);
                message = "\n" + "Error:" + e.getMessage();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            Log.d("Your DNS response message here", message);
            handleDNSResponse(message);
        }

        private String sendAndPrint(Resolver vr, String name) throws IOException {
            String result = "\nURL: " + name + "\n";
            Record qr = Record.newRecord(Name.fromConstantString(name), Type.A, DClass.IN);
            Message response = vr.send(Message.newQuery(qr));
            for (RRset set : response.getSectionRRsets(Section.ADDITIONAL)) {
                if (set.getName().equals(Name.root) && set.getType() == Type.TXT
                        && set.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
                    System.out.println("Reason:  " + ((TXTRecord) set.first()).getStrings().get(0));
                    result += "Reason:  " + ((TXTRecord) set.first()).getStrings().get(0) + "\n";
                }
            }

            return response.toString();
        }
    }



    private void handleDNSResponse(String answer) {
        Log.d("DNS Response", answer.toString());
        result.set("DNS Response: \n\n" + answer.toString());
    }

    private void handleDNSResolutionError(Exception ex) {
        Log.e("DNS Resolution Error", ex.toString());
        // Handle the error gracefully, e.g., display an error message to the user
        result.set("DNS Resolution Error: " + ex.toString());
    }

    private void handleGenericError(Exception e) {
        Log.e("Error", e.toString());
        // Handle the error gracefully, e.g., display an error message to the user
        result.set("Error" + e.toString());
    }
}