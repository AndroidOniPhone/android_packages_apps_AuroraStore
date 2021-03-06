package com.aurora.store.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aurora.store.AuroraApplication;
import com.aurora.store.enums.ErrorType;
import com.aurora.store.iterator.CustomAppListIterator;
import com.aurora.store.manager.FilterManager;
import com.aurora.store.model.App;
import com.aurora.store.task.SearchTask;
import com.aurora.store.util.NetworkUtil;
import com.aurora.store.viewmodel.BaseViewModel;
import com.dragons.aurora.playstoreapiv2.SearchIterator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchAppsModel extends BaseViewModel {

    protected CustomAppListIterator iterator;
    protected SearchIterator searchIterator;
    protected MutableLiveData<List<App>> listMutableLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<String>> relatedMutableLiveData = new MutableLiveData<>();

    public SearchAppsModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<String>> getRelatedTags() {
        return relatedMutableLiveData;
    }

    public LiveData<List<App>> getQueriedApps() {
        return listMutableLiveData;
    }

    public void fetchQueriedApps(String query, boolean shouldIterate) {

        if (!NetworkUtil.isConnected(getApplication())) {
            errorTypeMutableLiveData.setValue(ErrorType.NO_NETWORK);
            return;
        }

        api = AuroraApplication.api;
        if (!shouldIterate)
            getIterator(query);

        Disposable disposable = Observable.fromCallable(() -> new SearchTask(getApplication())
                .getSearchResults(iterator))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((appList) -> {
                    listMutableLiveData.setValue(appList);
                }, err -> handleError(err));
        compositeDisposable.add(disposable);
    }

    public void getIterator(String query) {
        try {
            searchIterator = new SearchIterator(api, query);
            iterator = new CustomAppListIterator(searchIterator);
            iterator.setFilterEnabled(true);
            iterator.setFilterModel(FilterManager.getFilterPreferences(getApplication()));
            //relatedMutableLiveData.setValue(iterator.getRelatedTags());
        } catch (Exception err) {
            handleError(err);
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
