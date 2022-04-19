package com.mrnadimi.searchview;

/**
 * Developer: Mohamad Nadimi
 * Company: Saghe
 * Website: https://www.mrnadimi.com
 * Created on 11 April 2022
 * <p>
 * Description: ...
 */
abstract class SearchRunnable implements Runnable {
    private String searchWord;

    SearchRunnable(String searchWord) {
        this.searchWord = searchWord;
    }

    String getSearchWord() {
        return searchWord;
    }
}
