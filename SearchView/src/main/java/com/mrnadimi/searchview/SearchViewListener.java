package com.mrnadimi.searchview;

import android.text.TextWatcher;

import androidx.annotation.UiThread;

import java.util.List;

/**
 * Developer: Mohamad Nadimi
 * Company: Saghe
 * Website: https://www.mrnadimi.com
 * Created on 11 April 2022
 * <p>
 * Description: ...
 */
public abstract class SearchViewListener<T> {

    public abstract void beforeSearch();

    /*
     * Zamanihast ke loading ra piadesazikardim ama be har dalil be pasokh naresidim
     * in method be ma neshan midahad ke be har dalili ye amaliat be payan naresid
     */
    //public abstract void onStopedBeforeResult();

    public abstract List<T> onSearch(String word) throws Exception;

    @UiThread
    public abstract void onResult(String word, List<T> t);

    @UiThread
    public abstract void onError(Exception ex);

    abstract TextWatcher getTextWatcher(final SearchView searchView);

    /*
     * Malasan word "mohamad" ra Search kardim va hala dar
     * scroll recycleview mikhahim page ra anjam dahim , chonn
     * kalame taghir nemikonad be hamin dalil TextWatcher
     * dar inja mojadadan farakhani nemishavad , tavasote in method ma
     * TextWatcher ra mojadadan faal mikonim ke dobare amaliate
     * search anjam dahad
     */
    abstract void notifyUpdate();

    abstract void dispose();
}
