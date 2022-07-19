package com.printurth;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.divyanshu.draw.widget.CircleView;
import com.divyanshu.draw.widget.DrawView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.printurth.gvr.ModelGvrActivity;
import com.printurth.obj.ObjModel;
import com.printurth.ply.PlyModel;
import com.printurth.stl.StlModel;
import com.printurth.util.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Copyright 2017 Dmitry Brant. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class STLViewActivity extends AppCompatActivity {
    private static final int READ_PERMISSION_REQUEST = 100;
    private static final int OPEN_DOCUMENT_REQUEST = 101;

    private static final String[] SAMPLE_MODELS
            = new String[] { "bunny.stl", "dragon.stl", "lucy.stl" };
    private static int sampleModelIndex;

    private ModelViewerApplication app;
    @Nullable private ModelSurfaceView modelView;
    private ViewGroup containerView;
    private ProgressBar progressBar;
    private RelativeLayout model_progress_bg;
    Intent intent;
    @BindView(R.id.back_btn)
    Button back_btn;
    @OnClick(R.id.back_btn)
    public void back_btnClicked() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back_btn.setVisibility(View.INVISIBLE);
    }


    @BindView(R.id.rl_bottom_sheet)
    LinearLayout rlBottomSheet;

    @BindView(R.id.view_anglebtn)
    ImageView sheetHandle;

    public boolean isSlideFin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stlview);
        ButterKnife.bind(this);
        back_btn.setVisibility(View.VISIBLE);
        app = ModelViewerApplication.getInstance();

        containerView = findViewById(R.id.container_view);
        progressBar = findViewById(R.id.model_progress_bar);
        model_progress_bg = findViewById(R.id.model_progress_bg);
        model_progress_bg.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

     //   findViewById(R.id.vr_fab).setOnClickListener((View v) -> startVrActivity());

        intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        beginLoadModel(uri);
        if (getIntent().getData() != null && savedInstanceState == null) {
            beginLoadModel(getIntent().getData());
        }

        setUpDrawTools();

        colorSelector();

        setPaintAlpha();

        setPaintWidth();

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        sheetHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSlideFin) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED );
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED );
                }

            }
        });



        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.e("dingding", "newState" + newState);
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    isSlideFin = true;
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isSlideFin = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {





            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        createNewModelView(app.getCurrentModel());
        if (app.getCurrentModel() != null) {
            setTitle(app.getCurrentModel().getTitle());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (modelView != null) {
            modelView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (modelView != null) {
            modelView.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open_model:
                checkReadPermissionThenOpen();
                return true;
            case R.id.menu_load_sample:
                loadSampleModel();
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beginOpenModel();
                } else {
                    Toast.makeText(this, R.string.read_permission_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == RESULT_OK && resultData.getData() != null) {
            Uri uri = resultData.getData();
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            beginLoadModel(uri);
        }
    }

    private void checkReadPermissionThenOpen() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION_REQUEST);
        } else {
            beginOpenModel();
        }
    }

    private void beginOpenModel() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
    }

    private void beginLoadModel(@NonNull Uri uri) {
        model_progress_bg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new ModelLoadTask().execute(uri);
    }

    private void createNewModelView(@Nullable Model model) {
        if (modelView != null) {
            containerView.removeView(modelView);
        }
        ModelViewerApplication.getInstance().setCurrentModel(model);
        modelView = new ModelSurfaceView(this, model);
        containerView.addView(modelView, 0);
    }

    private class ModelLoadTask extends AsyncTask<Uri, Integer, Model> {
        protected Model doInBackground(Uri... file) {
            InputStream stream = null;
            try {
                Uri uri = file[0];
                ContentResolver cr = getApplicationContext().getContentResolver();
                String fileName = getFileName(cr, uri);

                if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(uri.toString()).build();
                    Response response = client.newCall(request).execute();

                    // TODO: figure out how to NOT need to read the whole file at once.
                    stream = new ByteArrayInputStream(response.body().bytes());
                } else {
                    stream = cr.openInputStream(uri);
                }

                if (stream != null) {
                    Model model;
                    if (!TextUtils.isEmpty(fileName)) {
                        if (fileName.toLowerCase().endsWith(".stl")) {
                            model = new StlModel(stream);
                        } else if (fileName.toLowerCase().endsWith(".obj")) {
                            model = new ObjModel(stream);
                        } else if (fileName.toLowerCase().endsWith(".ply")) {
                            model = new PlyModel(stream);
                        } else {
                            // assume it's STL.
                            model = new StlModel(stream);
                        }
                        model.setTitle(fileName);
                    } else {
                        // assume it's STL.
                        // TODO: autodetect file type by reading contents?
                        model = new StlModel(stream);
                    }
                    return model;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Util.closeSilently(stream);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Model model) {
            if (isDestroyed()) {
                return;
            }
            if (model != null) {
                setCurrentModel(model);
            } else {
                Toast.makeText(getApplicationContext(), R.string.open_model_error, Toast.LENGTH_SHORT).show();
                model_progress_bg.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }

        @Nullable
        private String getFileName(@NonNull ContentResolver cr, @NonNull Uri uri) {
            if ("content".equals(uri.getScheme())) {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor metaCursor = ContentResolverCompat.query(cr, uri, projection, null, null, null, null);
                if (metaCursor != null) {
                    try {
                        if (metaCursor.moveToFirst()) {
                            return metaCursor.getString(0);
                        }
                    } finally {
                        metaCursor.close();
                    }
                }
            }
            return uri.getLastPathSegment();
        }
    }

    private void setCurrentModel(@NonNull Model model) {
        createNewModelView(model);
        Toast.makeText(getApplicationContext(), R.string.open_model_success, Toast.LENGTH_SHORT).show();
        setTitle(model.getTitle());
        model_progress_bg.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void startVrActivity() {
        if (app.getCurrentModel() == null) {
            Toast.makeText(this, R.string.view_vr_not_loaded, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, ModelGvrActivity.class));
        }
    }

    private void loadSampleModel() {
        try {
            InputStream stream = getApplicationContext().getAssets()
                    .open(SAMPLE_MODELS[sampleModelIndex++ % SAMPLE_MODELS.length]);
            setCurrentModel(new StlModel(stream));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.about_text)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @OnClick(R.id.view_frontbtn)
    public void view_frontbtnClicked() {
        modelView.setViewFront();
    }
    @OnClick(R.id.view_topbtn)
    public void view_topbtnClicked() {
        modelView.setViewTop();
    }

    @OnClick(R.id.view_rightbtn)
    public void view_rightbtnClicked() {
        modelView.setViewRight();
    }
    @OnClick(R.id.view_leftbtn)
    public void view_leftbtnClicked() {
        modelView.setViewLeft();
    }
    @OnClick(R.id.view_bottombtn)
    public void view_bottombtnClicked() {
        modelView.setViewBottom();
    }

    @OnClick(R.id.view_backbtn)
    public void view_backbtnClicked() {
        modelView.setViewBack();
    }


    //drawing

    private final void setUpDrawTools() {
        ((CircleView) findViewById(R.id.circle_view_opacity)).setCircleRadius(100.0F);
        ((ImageView)findViewById(R.id.image_draw_eraser)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ((DrawView) findViewById(R.id.draw_view)).toggleEraser();
                ImageView var10000 = (ImageView)findViewById(R.id.image_draw_eraser);
                Intrinsics.checkExpressionValueIsNotNull(var10000, "image_draw_eraser");
                var10000.setSelected(((DrawView)findViewById(R.id.draw_view)).isEraserOn());
                STLViewActivity var2 = STLViewActivity.this;
                ConstraintLayout var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                var2.toggleDrawTools((View)var10001, false);
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_eraser)).setOnLongClickListener((View.OnLongClickListener)(new View.OnLongClickListener() {
            public final boolean onLongClick(View it) {
                ((DrawView)findViewById(R.id.draw_view)).clearCanvas();
                STLViewActivity var10000 = STLViewActivity.this;
                ConstraintLayout var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                var10000.toggleDrawTools((View)var10001, false);
                return true;
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_width)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ConstraintLayout var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                ConstraintLayout var10001;
                STLViewActivity var2;
                SeekBar var3;
                if (var10000.getTranslationY() == STLViewActivity.this.getToPx(56)) {
                    var2 = STLViewActivity.this;
                    var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                    var2.toggleDrawTools((View)var10001, true);
                } else {
                    var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                    if (var10000.getTranslationY() == STLViewActivity.this.getToPx(0)) {
                        var3 = (SeekBar)findViewById(R.id.seekBar_width);
                        Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_width");
                        if (var3.getVisibility() == View.VISIBLE) {
                            var2 = STLViewActivity.this;
                            var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                            Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                            var2.toggleDrawTools((View)var10001, false);
                        }
                    }
                }

                CircleView var4 = (CircleView)findViewById(R.id.circle_view_width);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_width");
                var4.setVisibility(View.VISIBLE);
                var4 = (CircleView)findViewById(R.id.circle_view_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_opacity");
                var4.setVisibility(View.GONE);
                var3 = (SeekBar)findViewById(R.id.seekBar_width);
                Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_width");
                var3.setVisibility(View.VISIBLE);
                var3 = (SeekBar)findViewById(R.id.seekBar_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_opacity");
                var3.setVisibility(View.GONE);
                View var5 = findViewById(R.id.draw_color_palette);
                Intrinsics.checkExpressionValueIsNotNull(var5, "draw_color_palette");
                var5.setVisibility(View.GONE);
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_opacity)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ConstraintLayout var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                ConstraintLayout var10001;
                STLViewActivity var2;
                SeekBar var3;
                if (var10000.getTranslationY() == STLViewActivity.this.getToPx(56)) {
                    var2 = STLViewActivity.this;
                    var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                    var2.toggleDrawTools((View)var10001, true);
                } else {
                    var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                    if (var10000.getTranslationY() == STLViewActivity.this.getToPx(0)) {
                        var3 = (SeekBar)findViewById(R.id.seekBar_opacity);
                        Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_opacity");
                        if (var3.getVisibility() == View.VISIBLE) {
                            var2 = STLViewActivity.this;
                            var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                            Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                            var2.toggleDrawTools((View)var10001, false);
                        }
                    }
                }

                CircleView var4 = (CircleView)findViewById(R.id.circle_view_width);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_width");
                var4.setVisibility(View.GONE);
                var4 = (CircleView)findViewById(R.id.circle_view_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_opacity");
                var4.setVisibility(View.VISIBLE);
                var3 = (SeekBar)findViewById(R.id.seekBar_width);
                Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_width");
                var3.setVisibility(View.GONE);
                var3 = (SeekBar)findViewById(R.id.seekBar_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var3, "seekBar_opacity");
                var3.setVisibility(View.VISIBLE);
                View var5 = findViewById(R.id.draw_color_palette);
                Intrinsics.checkExpressionValueIsNotNull(var5, "draw_color_palette");
                var5.setVisibility(View.GONE);
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_color)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ConstraintLayout var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                ConstraintLayout var10001;
                STLViewActivity var2;
                View var3;
                if (var10000.getTranslationY() == STLViewActivity.this.getToPx(56)) {
                    var2 = STLViewActivity.this;
                    var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                    var2.toggleDrawTools((View)var10001, true);
                } else {
                    var10000 = (ConstraintLayout)findViewById(R.id.draw_tools);
                    Intrinsics.checkExpressionValueIsNotNull(var10000, "draw_tools");
                    if (var10000.getTranslationY() == STLViewActivity.this.getToPx(0)) {
                        var3 = findViewById(R.id.draw_color_palette);
                        Intrinsics.checkExpressionValueIsNotNull(var3, "draw_color_palette");
                        if (var3.getVisibility() == View.VISIBLE) {
                            var2 = STLViewActivity.this;
                            var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                            Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                            var2.toggleDrawTools((View)var10001, false);
                        }
                    }
                }

                CircleView var4 = (CircleView)findViewById(R.id.circle_view_width);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_width");
                var4.setVisibility(View.GONE);
                var4 = (CircleView)findViewById(R.id.circle_view_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var4, "circle_view_opacity");
                var4.setVisibility(View.GONE);
                SeekBar var5 = (SeekBar)findViewById(R.id.seekBar_width);
                Intrinsics.checkExpressionValueIsNotNull(var5, "seekBar_width");
                var5.setVisibility(View.GONE);
                var5 = (SeekBar)findViewById(R.id.seekBar_opacity);
                Intrinsics.checkExpressionValueIsNotNull(var5, "seekBar_opacity");
                var5.setVisibility(View.GONE);
                var3 = findViewById(R.id.draw_color_palette);
                Intrinsics.checkExpressionValueIsNotNull(var3, "draw_color_palette");
                var3.setVisibility(View.VISIBLE);
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_undo)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ((DrawView)findViewById(R.id.draw_view)).undo();
                STLViewActivity var10000 = STLViewActivity.this;
                ConstraintLayout var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                var10000.toggleDrawTools((View)var10001, false);
            }
        }));
        ((ImageView)findViewById(R.id.image_draw_redo)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                ((DrawView)findViewById(R.id.draw_view)).redo();
                STLViewActivity var10000 = STLViewActivity.this;
                ConstraintLayout var10001 = (ConstraintLayout)findViewById(R.id.draw_tools);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "draw_tools");
                var10000.toggleDrawTools((View)var10001, false);
            }
        }));
    }

    private final void toggleDrawTools(View view, boolean showView) {
        if (showView) {
            view.animate().translationY(this.getToPx(0));
        } else {
            view.animate().translationY(this.getToPx(56));
        }

    }

    // $FF: synthetic method
    static void toggleDrawTools$default(STLViewActivity var0, View var1, boolean var2, int var3, Object var4) {
        if ((var3 & 2) != 0) {
            var2 = true;
        }

        var0.toggleDrawTools(var1, var2);
    }

    private final void colorSelector() {
        ((ImageView)findViewById(R.id.image_color_black)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_black, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_black);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_black");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_red)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_red, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_red);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_red");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_yellow)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_yellow, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_yellow);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_yellow");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_green)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_green, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_green);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_green");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_blue)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_blue, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_blue);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_blue");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_pink)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_pink, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_pink);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_pink");
                var10000.scaleColorView((View)var10001);
            }
        }));
        ((ImageView)findViewById(R.id.image_color_brown)).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
            public final void onClick(View it) {
                int color = ResourcesCompat.getColor(STLViewActivity.this.getResources(), R.color.color_brown, (Resources.Theme)null);
                ((DrawView)findViewById(R.id.draw_view)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setColor(color);
                ((CircleView)findViewById(R.id.circle_view_width)).setColor(color);
                STLViewActivity var10000 = STLViewActivity.this;
                ImageView var10001 = (ImageView)findViewById(R.id.image_color_brown);
                Intrinsics.checkExpressionValueIsNotNull(var10001, "image_color_brown");
                var10000.scaleColorView((View)var10001);
            }
        }));
    }

    private final void scaleColorView(View view) {
        ImageView var10000 = (ImageView)findViewById(R.id.image_color_black);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_black");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_black);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_black");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_red);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_red");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_red);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_red");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_yellow);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_yellow");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_yellow);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_yellow");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_green);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_green");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_green);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_green");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_blue);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_blue");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_blue);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_blue");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_pink);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_pink");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_pink);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_pink");
        var10000.setScaleY(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_brown);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_brown");
        var10000.setScaleX(1.0F);
        var10000 = (ImageView)findViewById(R.id.image_color_brown);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "image_color_brown");
        var10000.setScaleY(1.0F);
        view.setScaleX(1.5F);
        view.setScaleY(1.5F);
    }

    private final void setPaintWidth() {
        ((SeekBar)findViewById(R.id.seekBar_width)).setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener)(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(@org.jetbrains.annotations.Nullable SeekBar seekBar, int progress, boolean fromUser) {
                ((DrawView)findViewById(R.id.draw_view)).setStrokeWidth((float)progress);
                ((CircleView)findViewById(R.id.circle_view_width)).setCircleRadius((float)progress);
            }

            public void onStartTrackingTouch(@org.jetbrains.annotations.Nullable SeekBar seekBar) {
            }

            public void onStopTrackingTouch(@org.jetbrains.annotations.Nullable SeekBar seekBar) {
            }
        }));
    }

    private final void setPaintAlpha() {
        ((SeekBar)findViewById(R.id.seekBar_opacity)).setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener)(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(@org.jetbrains.annotations.Nullable SeekBar seekBar, int progress, boolean fromUser) {
                ((DrawView)findViewById(R.id.draw_view)).setAlpha(progress);
                ((CircleView)findViewById(R.id.circle_view_opacity)).setAlpha(progress);
            }

            public void onStartTrackingTouch(@org.jetbrains.annotations.Nullable SeekBar seekBar) {
            }

            public void onStopTrackingTouch(@org.jetbrains.annotations.Nullable SeekBar seekBar) {
            }
        }));
    }

    private final float getToPx(int $this$toPx) {
        float var10000 = (float)$this$toPx;
        Resources var10001 = Resources.getSystem();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "Resources.getSystem()");
        return var10000 * var10001.getDisplayMetrics().density;
    }


    
    
}
