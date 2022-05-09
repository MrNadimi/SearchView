package com.mrnadimi.searchview;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Developer: Mohamad Nadimi
 * Company: Saghe
 * Website: https://www.mrnadimi.com
 * Created on 11 April 2022
 * <p>
 * Description: ...
 */ /*
 * In modell Search mamulan baraye online estefade mishavad ve har search
 * tedade mahdudi manande 50 ya 100 ta darad va mamulan baraye paging javab nemidahad
 * chon cache mikonad search haye mar ra va in baese keak shodane
 * hafezemishavad
 */
public abstract class SearchWithoutPagingListener<T> extends SearchViewListener<T> {
    /*
     * Search hai ke anjam shode va be pasokh reside ra dar inja
     * zakhire mikonim ta dar surate search mojadad haman kalame
     * zaman ra bekharim
     */
    private final Map<String, List<T>> lastCachedResults;
    /*
     * search haye dar hale anjam  ra dar inja ghrara midahad ta dobare search nashavad va
     * montazer pasokh bemanad
     *
     * Mavaghei masalam mikhahim kalameye "mohamad" ra search konim
     * dar ebteda "m" va sepas "mo" va ... type mishavad ta be "mohamad" beresim
     * ba neveshtane har character search anjam mishavad va zamani talaf mishavad
     * inkar baes mishavad ta zaman ra bekharim va vaghti dar hale pak kardane "mohamad"
     * hastim digar mojadadam "mohama" va "moham" va .. search nashavad
     */
    private final Map<String, Boolean> activeRequests;

    private String beforeText;
    private TextWatcher textWatcher;


    public SearchWithoutPagingListener() {
        lastCachedResults = new HashMap<>();
        activeRequests = new HashMap<>();
    }


    TextWatcher getTextWatcher(final SearchView searchView) {
        if (textWatcher != null) return textWatcher;
        this.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s != null) {
                        String query = s.toString();

                        if (beforeText != null && !beforeText.isEmpty()) {
                            activeRequests.put(beforeText, false);
                        }

                        if (query.isEmpty()) {
                            if (canRevokeOnResult(searchView)) {
                                onResult(query, new ArrayList<>());
                            }
                            return;
                        }

                        activeRequests.put(query, true);

                        if (lastCachedResults.containsKey(query)) {
                            if (canRevokeOnResult(searchView)) {
                                onResult(query, lastCachedResults.get(query));
                            }
                            return;
                        }




                        Boolean b = activeRequests.get(query);
                        if (b != null && !b) {
                            return;
                        }
                        Thread t = new Thread(new SearchRunnable(query) {
                            @Override
                            public void run() {
                                try {
                                    searchView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                beforeSearch();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                    final List<T> temp = onSearch(getSearchWord());

                                    //save result in memory
                                    lastCachedResults.put(getSearchWord(), temp);

                                    //return if search exist on memory >maybe not important
                                    Boolean b = activeRequests.get(getSearchWord());
                                    if (b != null && !b) {
                                        return;
                                    }

                                    searchView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (canRevokeOnResult(searchView)) {
                                                    onResult(getSearchWord(), temp);
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });

                                } catch (final Exception ex) {
                                    searchView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                onError(ex);
                                            } catch (Exception e) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        t.setPriority(Thread.MAX_PRIORITY);
                        t.start();
                    } else {
                        if (canRevokeOnResult(searchView)) {
                            onResult("", new ArrayList<>());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        return textWatcher;
    }

    /*
     * Important
     * Agar visible budan search view ra check nakonim bad az
     * pause va resume shodan in fragment mojadadan
     * meghdare search view dar TextWacher farakhani shode
     * va be in method miresad ke vaghti search view faal nist
     * baese taghir dar mahiate adapter mishavad
     */
    private boolean canRevokeOnResult(SearchView searchView) {
        if (searchView.getVisibility() != View.VISIBLE) {
            Log.w("SearchView", "The searchView is not visible");
            return false;
        } else
            return true;
    }

    synchronized void notifyUpdate() {

    }



    void dispose() {
        try {
            this.activeRequests.clear();
            this.lastCachedResults.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
