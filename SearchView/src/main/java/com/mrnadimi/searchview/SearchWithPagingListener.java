package com.mrnadimi.searchview;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Developer: Mohamad Nadimi
 * Company: Saghe
 * Website: https://www.mrnadimi.com
 * Created on 11 April 2022
 * <p>
 * Description: ...
 */ /*
 * In search cache nadarad va mamulan az database
 * search mikonad va mitavan ba an paging kard
 * mamaluan dar in search paging mitavanad ziad bashad be hamin
 * dalil cache kare manteghi nist chon momken ast tedade cache besyar ziad bashad
 */
public abstract class SearchWithPagingListener<T> extends SearchViewListener<T> {


    private String beforeText, currentText;
    private TextWatcher textWatcher;
    private final AtomicBoolean onUpdateNotify;
    private SearchView searchView;
    private String lastTextForNotify;

    public SearchWithPagingListener() {
        onUpdateNotify = new AtomicBoolean(false);
        this.lastTextForNotify = "";
    }

    TextWatcher getTextWatcher(final SearchView searchView) {
        this.searchView = searchView;
        if (textWatcher != null) return textWatcher;
        this.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (onUpdateNotify.get()) return;
                beforeText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onUpdateNotify.get()) {
                    onUpdateNotify.set(false);
                    searchView.getEditText().setText(lastTextForNotify);
                    searchView.getEditText().setSelection(lastTextForNotify.length());
                    lastTextForNotify = "";
                    return;
                }
                try {
                    if (s != null) {
                        String query = s.toString();
                        /*
                         * Word is changed
                         */
                        if (!query.equals(currentText)) {
                            final String lastWord = currentText;
                            searchView.post(new Runnable() {
                                @Override
                                public void run() {
                                    onWordChanged(lastWord, query);
                                }
                            });
                        }
                        currentText = query;
                        if (query.isEmpty()) {
                            if (canRevokeOnResult(searchView)) {
                                onResult(query, null);
                            }
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
                                    /*
                                     * Kalameye jadid search shode va nabayad in natije ersal shavad
                                     */
                                    if (!getSearchWord().equals(currentText)) {
                                        return;
                                    }

                                    searchView.post(() -> {
                                        try {
                                            if (canRevokeOnResult(searchView)) {
                                                onResult(getSearchWord(), temp);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
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
                            onResult("", null);
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
            Log.w("SearchView-", "The searchView is not visible--");
            return false;
        } else
            return true;
    }

    public abstract void onWordChanged(String lastWord, String currentWord);


    void notifyUpdate() {
        if (this.searchView == null) return;
        onUpdateNotify.set(true);
        lastTextForNotify = this.searchView.getEditText().getText().toString();
        this.searchView.getEditText().setText("");
    }

    void dispose() {
        onUpdateNotify.set(false);
        lastTextForNotify = "";
        currentText = null;
    }
}
