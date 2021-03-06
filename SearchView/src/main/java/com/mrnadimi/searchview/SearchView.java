package com.mrnadimi.searchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;


/**
 * Developer: Mohamad Nadimi
 * Company: Saghe
 * Website: https://www.mrnadimi.com
 * Created on 11 April 2022
 * <p>
 * Description: ...
 */

public class SearchView extends RelativeLayout {

    private EditText editText;
    private ImageView search, back;
    private SearchViewListener<?> searchListener;

    public SearchView(Context context) {
        super(context);
        init(context ,null , null , null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context ,attrs , null , null);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs , defStyleAttr,null);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr,defStyleRes);
    }

    private void init(Context context , AttributeSet attributeSet , Integer defStyleAttr , Integer defStyleRes){
        initDefault(context);
        if (attributeSet == null){
            return;
        }
        defStyleAttr = (defStyleAttr == null) ? 0 : defStyleAttr;
        defStyleRes = (defStyleRes == null) ? 0 : defStyleRes;
        TypedArray a = context.obtainStyledAttributes(attributeSet,
                R.styleable.SearchView, defStyleAttr, defStyleRes);


        //background
        int background = a.getResourceId(R.styleable.SearchView_sv_backgroundResource , R.drawable.style_searchview_background);
        setBackgroundResource(background);
        //end

        //background
        int backButtonResource = a.getResourceId(R.styleable.SearchView_sv_backButtonIcon , R.drawable.ic_arrow_back_search_view_24dp);
        back.setImageResource(backButtonResource);
        //end

        //background
        int searchButtonResource = a.getResourceId(R.styleable.SearchView_sv_searchButtonIcon , R.drawable.ic_search_search_view_24dp);
        search.setImageResource(searchButtonResource);
        //end


        /*
         * Edittext hint
         */
        String hint = a.getString(R.styleable.SearchView_sv_hint);
        if (hint != null)
            editText.setHint(hint);
        //end

        /*
         * Show back btn
         */
        boolean showBackBtn = a.getBoolean(R.styleable.SearchView_sv_showBackButton, true);
        if (!showBackBtn){
            this.back.setVisibility(GONE);
        }
        //end

        /*
         * Show search icon
         */
        boolean showSearchIcon = a.getBoolean(R.styleable.SearchView_sv_showSearchIcon, true);
        if (!showSearchIcon){
            this.search.setVisibility(GONE);
        }
        //end



        a.recycle();
    }

    private void initDefault(Context context){
        setGravity(CENTER_VERTICAL);
        createBackBtn(context);
        createSearchBtn(context );
        createEditText(context);

    }

    //Fix height
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getToolBarHeightInPixel(), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private void createBackBtn(Context context){
        this.back = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , getToolBarHeightInPixel());
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        int backPadding = getDimenInPixel(R.dimen.size_10dp);
        this.back.setPadding( backPadding,backPadding,backPadding,backPadding );
        params.setMarginStart(getDimenInPixel(R.dimen.size_5dp));
        this.back.setLayoutParams(params);
        this.back.setId(View.generateViewId());
        this.back.setBackgroundResource(getSelectableBackground());
        addView(this.back);
    }




    private void createEditText(Context context){
        this.editText = new EditText(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT
                , getToolBarHeightInPixel() );
        params.setMarginStart(getDimenInPixel(R.dimen.size_10dp));
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.END_OF, this.back.getId() );
        params.addRule(RelativeLayout.START_OF, this.search.getId() );
        this.editText.setBackgroundResource(R.color.sv_transparent);
        this.editText.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        this.editText.setLayoutParams(params);
        this.editText.setId(View.generateViewId());
        /*
         * Text showed on center when language is rtl
         */
        this.editText.setPadding(0,0,0,0);
        this.editText.setMaxLines(1);
        //to show search in device keyboard
        editText.setLines(1);
        editText.setSingleLine(true);
        this.editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //end
        this.editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        /*
         * Important: when write string as ltr and rtl together
         */
        this.editText.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        this.editText.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        //End
        addView(this.editText);
    }

    private void createSearchBtn(Context context) {
        this.search = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                ,getToolBarHeightInPixel());
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.setMarginEnd(getDimenInPixel(R.dimen.size_5dp));
        params.addRule(RelativeLayout.ALIGN_PARENT_END, 1 );
        int searchPadding = getDimenInPixel(R.dimen.size_10dp);
        this.search.setPadding( searchPadding,searchPadding,searchPadding,searchPadding );
        this.search.setLayoutParams(params);
        this.search.setId(View.generateViewId());
        this.search.setBackgroundResource(getSelectableBackground());
        addView(this.search);
    }


    private int getDimenInPixel(@DimenRes int id){
        return (int) getResources().getDimension(id);
    }

    private int getToolBarHeightInPixel() {
        int[] attrs = new int[] {android.R.attr.actionBarSize};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    private int getSelectableBackground(){
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        return outValue.resourceId;

    }

    /*
     * Change back button image
     */
    public void setBackButtonResource(@DrawableRes int id){
        this.back.setImageResource(id);
    }

    public void setBackButtonOnClickListener(OnClickListener listener){
        this.back.setOnClickListener(listener);
    }

    @IdRes
    public int getBackButtonViewId(){
        return this.back.getId();
    }

    public void showBackButton(boolean show){
        this.back.setVisibility(show ? VISIBLE : GONE);
    }

    /*
     * Baraye show shodan progress ra hidemikonad
     */
    public void showSearchIcon(boolean show){
        this.search.setVisibility(show ? VISIBLE : GONE);
    }


    public EditText getEditText(){
        return this.editText;
    }

    public ImageView getBackButton(){
        return back;
    }

    public void setHint(String hint){
        this.editText.setHint(hint);
    }

    public void setHint(@StringRes int hint){
        this.editText.setHint(hint);
    }

    public void notifyUpdate(){
        if (this.searchListener != null) {
            this.searchListener.notifyUpdate();
        }
    }

    public void dispose(){
        if (this.searchListener != null) {
            setSearchListener(null);
            this.searchListener.dispose();
            this.searchListener = null;
        }
    }


    public void showFocus(boolean focus){
        if (focus) {
            this.editText.requestFocus();
        } else {
            this.editText.clearFocus();
        }
    }


    public void setSearchListener(SearchViewListener<?> searchListener){
        if (searchListener == null ){
            if (this.searchListener != null) {
                this.editText.removeTextChangedListener(this.searchListener.getTextWatcher(this));
            }
            return;
        }
        this.searchListener = searchListener;
        this.editText.addTextChangedListener(searchListener.getTextWatcher(this));
    }

    public SearchViewListener<?> getSearchListener() {
        return searchListener;
    }

    public void startFocus(){
        editText.performClick();
        editText.requestFocus();
    }

    /**
     * set text without callback
     */
    public void setForceText(String text){
        if (this.searchListener != null){
            this.editText.removeTextChangedListener(this.searchListener.getTextWatcher(this));
            setText(text);
            this.editText.addTextChangedListener(searchListener.getTextWatcher(this));
        }else{
            setText(text);
        }
    }

    public void setText(CharSequence text){
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

    public void clearText(){
        editText.setText("");
    }



}
