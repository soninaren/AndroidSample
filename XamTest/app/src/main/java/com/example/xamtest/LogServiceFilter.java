package com.example.xamtest;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

public class LogServiceFilter implements ServiceFilter {

    @Override
    public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

        final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

        String content = request.getContent();
        if (content == null)
            content = "NULL";

        String url = request.getUrl();
        if (url == null)
            url = "";

        Log.d("REQUEST URL", url);
        Log.d("REQUEST CONTENT", content);

        ListenableFuture<ServiceFilterResponse> nextServiceFilterCallbackFuture = nextServiceFilterCallback.onNext(request);

        Futures.addCallback(nextServiceFilterCallbackFuture, new FutureCallback<ServiceFilterResponse>() {

            @Override
            public void onFailure(Throwable exception) {
                final MobileServiceException mEx = (MobileServiceException) exception;
                int responseCode = mEx.getResponse().getStatus().getStatusCode();
                Log.d("RESPONSE CONTENT", Integer.toString(responseCode));
                resultFuture.setException(exception);
            }

            @Override
            public void onSuccess(ServiceFilterResponse response) {
                if (response != null) {
                    String content = response.getContent();
                    if (content != null) {
                        Log.d("RESPONSE CONTENT", content);
                    }
                }

                resultFuture.set(response);
            }
        });

        return resultFuture;
    }
}
