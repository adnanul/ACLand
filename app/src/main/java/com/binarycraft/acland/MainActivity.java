package com.binarycraft.acland;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.binarycraft.acland.adapter.SpinnerAdapter;
import com.binarycraft.acland.customcontrols.FloatingHintEditText;
import com.binarycraft.acland.datautil.DBHelper;
import com.binarycraft.acland.datautil.Data;
import com.binarycraft.acland.datautil.GetAndSaveData;
import com.binarycraft.acland.entity.Mouza;
import com.binarycraft.acland.entity.Union;
import com.binarycraft.acland.entity.UnionMouzaResponse;
import com.binarycraft.acland.entity.UpdateUnionMouzaResponse;
import com.binarycraft.acland.entity.VerifyDagResponse;
import com.binarycraft.acland.interfaces.APIUnionMouzaInterface;
import com.binarycraft.acland.util.ApplicationUtility;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Vector;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends Activity implements OnEditorActionListener {

    private FloatingHintEditText etSearchText;
    private ImageView ivSearch;
    private TextView tvStatusResult, tvStatus;
    private RadioGroup rgDaagType;
    private ProgressBar pbSearch;
    private MaterialBetterSpinner mbsUnion, mbsMouja;

    String previous_mouza, previous_union;

    protected MenuItem refreshItem = null;

    private boolean isAvailable = false;

    private String key = "", daagType;

    APIUnionMouzaInterface service;

    SpinnerAdapter unionSpinnerAdapter, mouzaSpinnerAdapter;

    DBHelper dbHelper;
    Vector<Union> unions;
    Vector<Mouza> mouzas;
    Vector<String> union_names;
    Vector<String> selectedMouzas;
    String mouzaId, unionId;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        dbHelper = new DBHelper(context);
        loadAllData();
        initUI();
        addClickListener();
        addTextWatcherForSearchBox();
        addTextChangeListener();
    }

    private void initUI() {
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "font/rupali.ttf");
        Log.e("Typeface", tf.toString());
        etSearchText = (FloatingHintEditText) findViewById(R.id.etSearch);
        etSearchText.setTypeface(tf);
        etSearchText.setOnEditorActionListener(this);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        tvStatusResult = (TextView) findViewById(R.id.tvStatusResult);
        tvStatusResult.setVisibility(View.GONE);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        mbsUnion = (MaterialBetterSpinner) findViewById(R.id.spUnion);
        mbsMouja = (MaterialBetterSpinner) findViewById(R.id.spMouza);
        mbsMouja.setFocusable(false);
        rgDaagType = (RadioGroup) findViewById(R.id.rgDaagType);
        pbSearch = (ProgressBar) findViewById(R.id.pbSearch);
        union_names = GetAndSaveData.getNamesFromUnions(unions);
        setUnionAdapter();
//        setMouzaAdapter();
    }

    private void setUnionAdapter() {
        unionSpinnerAdapter = new SpinnerAdapter(context, R.layout.spinner_row, union_names);
        mbsUnion.setAdapter(unionSpinnerAdapter);
        unionSpinnerAdapter.notifyDataSetChanged();
    }

    private void setMouzaAdapter() {
        selectedMouzas = GetAndSaveData.getNamesFromMouzas(mouzas);
        mouzaSpinnerAdapter = new SpinnerAdapter(context, R.layout.spinner_row, selectedMouzas);
        mbsMouja.setAdapter(mouzaSpinnerAdapter);
        mouzaSpinnerAdapter.notifyDataSetChanged();
    }

    private void addClickListener() {
        ivSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (validateUI())
                    searchAction();
            }
        });

        mbsMouja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText(getString(R.string.information));
                tvStatus.setTextColor(getResources().getColor(R.color.black));
                etSearchText.setError(null);
            }
        });

        mbsUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setTextColor(getResources().getColor(R.color.black));
                tvStatus.setText(getString(R.string.information));
                etSearchText.setError(null);
            }
        });
    }

    private void addTextWatcherForSearchBox() {
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvStatusResult.setVisibility(View.GONE);
                tvStatus.setTextColor(getResources().getColor(R.color.Black));
                if (count < 1) {
                    tvStatus.setText(getString(R.string.information));
                    tvStatus.setTextSize(14);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchAction() {
        key = etSearchText.getText().toString();
        key = ApplicationUtility.isEnglishNumber(key);
        int selectedrbid = rgDaagType.getCheckedRadioButtonId();
        if (selectedrbid == R.id.rbRs) {
            daagType = Data.RS_DAG;
        } else {
            daagType = Data.SA_DAG;
        }
        Log.e("Parameters", "Dag Number: " + key + ", Mouza: " + mouzaId + ", dagType: " + daagType + ", Union_ID: " + unionId);
        pbSearch.setVisibility(View.VISIBLE);
        tvStatusResult.setVisibility(View.GONE);
        tvStatus.setText(getString(R.string.waiting_text));
        initWebSevice();
    }

    private void initWebSevice() {
        if (ApplicationUtility.checkInternet(context)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Data.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(APIUnionMouzaInterface.class);
            if (TextUtils.isEmpty(unionId)) {
                //Show error on union spinner
                mbsUnion.setError(getString(R.string.tohshil_error));
                pbSearch.setVisibility(View.GONE);
                tvStatus.setText(getString(R.string.tohshil_error));
                tvStatus.setTextColor(getResources().getColor(R.color.Red));
            } else if (TextUtils.isEmpty(mouzaId)) {
                //Show error on Mouza Spinner
                mbsMouja.setError(getString(R.string.mouja_error));
                pbSearch.setVisibility(View.GONE);
                tvStatus.setText(getString(R.string.mouja_error));
                tvStatus.setTextColor(getResources().getColor(R.color.Red));
            } else if (TextUtils.isEmpty(mouzaId) && TextUtils.isEmpty(unionId)) {
                //Show error on Mouza and union spinner
                mbsUnion.setError(getString(R.string.tohshil_error));
                mbsMouja.setError(getString(R.string.mouja_error));
                pbSearch.setVisibility(View.GONE);
                tvStatus.setText(getString(R.string.tohshil_mouza_error));
                tvStatus.setTextColor(getResources().getColor(R.color.Red));
            } else {
                Call<VerifyDagResponse> call = service.verifyDasNumber(mouzaId, unionId, key, daagType);
                call.enqueue(verifyDagnumberResponseCallback);
            }
        } else {
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    Callback<VerifyDagResponse> verifyDagnumberResponseCallback = new Callback<VerifyDagResponse>() {

        @Override
        public void onResponse(Response<VerifyDagResponse> response, Retrofit retrofit) {
            Log.e("Response", response.body().toString());
            showResult(response.body());
        }

        @Override
        public void onFailure(Throwable t) {

        }
    };

    private void showResult(VerifyDagResponse verifyDagResponse) {
        Log.e("Response", verifyDagResponse.getStatus());
        String dagDescription = verifyDagResponse.getDescription();
        String status = verifyDagResponse.getStatus();
        pbSearch.setVisibility(View.GONE);
        if (status.equalsIgnoreCase("false")) {
            tvStatusResult.setVisibility(View.GONE);
            isAvailable = false;
            tvStatus.setText(key + " " + getString(R.string.not_available));
            tvStatus.setTextColor(getResources().getColor(R.color.Red));
            tvStatus.setTextSize(14);
        } else {
            isAvailable = true;
            if (key.equalsIgnoreCase("chut")) {
                tvStatus.setText(getString(R.string.chut) + " " + getString(R.string.available));
            } else {
                tvStatus.setText(key + " " + getString(R.string.available));
            }
            tvStatus.setTextColor(getResources().getColor(R.color.Black));
            tvStatus.setTextSize(14);
            tvStatusResult.setVisibility(View.VISIBLE);
            tvStatusResult.setTextColor(getResources().getColor(R.color.Green));
            tvStatusResult.setTextSize(14);
            tvStatusResult.setText(dagDescription);

        }
    }

    private void addTextChangeListener() {
        mbsUnion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previous_union = s + "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Item", s.toString());
                mbsMouja.setFocusableInTouchMode(true);
                loadMouzas(s.toString());
                setMouzaAdapter();
                setUnionId(s.toString());
                String union = s + "";
                mbsMouja.setText("");
                tvStatus.setText(getString(R.string.information));
                tvStatus.setTextColor(getResources().getColor(R.color.black));
                tvStatusResult.setVisibility(View.GONE);
                if (isMaizkhapon(s + "")) {
                    etSearchText.setFocusableInTouchMode(true);
                    ivSearch.setClickable(true);
                } else {
                    etSearchText.setFocusable(false);
                    ivSearch.setClickable(false);
                    tvStatus.setText(getString(R.string.information_add));
                    tvStatus.setTextColor(getResources().getColor(R.color.Red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mbsMouja.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("MOUZA", s + "");
                previous_mouza = s + "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mouzaId = getMouzaId(s.toString());
                Log.d("Mouza ID", mouzaId + " ");
                Log.e("MOUZA", s + "");
                String mouza = s + "";
                tvStatus.setText(getString(R.string.information));
                tvStatusResult.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isMaizkhapon(mbsUnion.getText().toString())) {
                    Log.e("MaizKhapon", "Not Maizkhapon");
                    tvStatus.setTextColor(getResources().getColor(R.color.Red));
                    tvStatus.setText(getString(R.string.information_add));
                } else {
                    tvStatus.setTextColor(getResources().getColor(R.color.black));
                    tvStatus.setText(getString(R.string.information));
                }
            }
        });
    }

    private boolean isMaizkhapon(String text) {
        if (text.equalsIgnoreCase(getString(R.string.maijkhapon))) {
            return true;
        } else {
            return false;
        }
    }

    private String getMouzaId(String mouzaName) {
        for (Mouza mouza :
                mouzas) {
            if (mouza.getName().equalsIgnoreCase(mouzaName)) {
                return mouza.getId();
            }
        }
        return "1";
    }

    private void setUnionId(String unionName) {
        for (Union union : unions
                ) {
            if (union.getName().equalsIgnoreCase(unionName)) {
                unionId = union.getId();
                break;
            }
        }
    }

    private void loadAllData() {
        unions = dbHelper.getAllUnions();
//        mouzas = dbHelper.getAllMouzas("");
    }

    private void loadMouzas(String unionName) {
        Log.e("Union Name", unionName);
        String uId = "";
        for (Union union :
                unions) {
            Log.e("Union Name", union.getName());
            if (union.getName().equalsIgnoreCase(unionName)) {
                Log.e("Union ID", union.getId() + " No");

                uId = union.getId();
            }
        }
        mouzas = dbHelper.getAllMouzas(uId);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null :
                getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean validateUI() {
        if (etSearchText.getText().toString().equals("")
                || etSearchText.getText().toString().equals(null)) {
            etSearchText.setError(getString(R.string.search_error));
            tvStatus.setText(getString(R.string.information));
            tvStatus.setTextSize(14);
            tvStatusResult.setVisibility(View.GONE);
            return false;
        } else
            return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            refreshItem = item;
            runRefresh();
            int countMouza = dbHelper.getMouzaCount();
            int countUnion = dbHelper.getUnionCount();
            initWebSeviceForRefresh(countMouza, countUnion);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
        if (arg1 == EditorInfo.IME_ACTION_DONE) {
            searchAction();
            return true;
        }
        return false;
    }


    private void initWebSeviceForRefresh(int countMouza, int countUnion) {
        if (ApplicationUtility.checkInternet(context)) {
            Log.e("Web Sevice", "ON");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Data.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(APIUnionMouzaInterface.class);
            Call<UpdateUnionMouzaResponse> call = service.getUpdatedUnionMouza(countUnion, countMouza);
            call.enqueue(unionMouzaResponseCallback);
        } else {
            ApplicationUtility.openNetworkDialog(this);
            stopRefresh();
        }
    }

    Callback<UpdateUnionMouzaResponse> unionMouzaResponseCallback = new Callback<UpdateUnionMouzaResponse>() {

        @Override
        public void onResponse(Response<UpdateUnionMouzaResponse> response, Retrofit retrofit) {
            boolean isMouzaUpdated = response.body().isMouzaUpdated();
            boolean isUnionUpdated = response.body().isUnionUpdated();
            Log.e("Message", response.body().getMessage().toString());
            if (isMouzaUpdated && !isUnionUpdated) {
                Log.e("Message", "Mouza Updated");
                updateMouzas(response.body().getData());
            } else if (isUnionUpdated && !isMouzaUpdated) {
                Log.e("Message", "Union Updated");
                updateUnions(response.body().getData());
            } else if (isMouzaUpdated && isUnionUpdated) {
                Log.e("Message", response.body().getMessage().toString());
                updateMouzas(response.body().getData());
                updateUnions(response.body().getData());
            } else {
//                Toast.makeText(context,response.body().getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
            stopRefresh();
        }

        @Override
        public void onFailure(Throwable t) {
            stopRefresh();
            Log.e("Request", "Failed");
            Toast.makeText(context, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
        }
    };

    private void updateMouzas(UnionMouzaResponse unionMouzaResponse) {
        mouzas = unionMouzaResponse.getMouzas();
        GetAndSaveData.saveMouzas(mouzas, dbHelper);
    }

    private void updateUnions(UnionMouzaResponse unionMouzaResponse) {
        unions = unionMouzaResponse.getUnions();
        Log.e("Union Size", union_names.size() + "");
        union_names.addAll(GetAndSaveData.getNamesFromUnions(unions));
        setUnionAdapter();
        Log.e("Union Size", union_names.size() + "");
        GetAndSaveData.saveUnions(unions, dbHelper);
    }

    protected void stopRefresh() {
        if (refreshItem != null) {
            refreshItem.setActionView(null);
        }
    }

    protected void runRefresh() {
        if (refreshItem != null) {
            refreshItem.setActionView(R.layout.indeterminate_progress_action);
        }
    }
}
