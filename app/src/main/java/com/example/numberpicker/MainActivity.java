package com.example.numberpicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public float firstItemWidthDate;
    public float paddingDate;
    public float itemWidthDate;
    public int allPixelsDate;
    public int finalWidthDate;
    private DateAdapter dateAdapter;
    private ArrayList<LabelerDate> labelerDates = new ArrayList<>();
     RecyclerView recyclerViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRecyclerviewDate();
    }


    public void getRecyclerviewDate() {
        recyclerViewDate = (RecyclerView) findViewById(R.id.recycler);
        if (recyclerViewDate != null) {
            recyclerViewDate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDateValue();
                }
            }, 300);
            /*recyclerViewDate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewDate.smoothScrollToPosition(dateAdapter.getItemCount()-1);
                    setDateValue();
                }
            }, 5000);*/
        }
        ViewTreeObserver vtoDate = recyclerViewDate.getViewTreeObserver();
        vtoDate.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {


            @Override
            public boolean onPreDraw() {
                recyclerViewDate.getViewTreeObserver().removeOnPreDrawListener(this);
                finalWidthDate = recyclerViewDate.getMeasuredWidth();
                itemWidthDate = getResources().getDimension(R.dimen.item_dob_width);
                paddingDate = (finalWidthDate - itemWidthDate) / 2;
                firstItemWidthDate = paddingDate;
                allPixelsDate = 0;

                final LinearLayoutManager dateLayoutManager = new LinearLayoutManager(getApplicationContext());
                dateLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewDate.setLayoutManager(dateLayoutManager);
                recyclerViewDate.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        synchronized (this) {
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                calculatePositionAndScrollDate(recyclerView);
                            }
                        }

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        allPixelsDate += dx;
                    }
                });
                if (labelerDates == null) {
                    labelerDates = new ArrayList<>();
                }
                genLabelerDate();
                dateAdapter = new DateAdapter(labelerDates, (int) firstItemWidthDate);
                recyclerViewDate.setAdapter(dateAdapter);
                dateAdapter.setSelecteditem(dateAdapter.getItemCount() - 1);
                return true;
            }
        });
    }

    private void genLabelerDate() {
        for (int i = 0; i < 32; i++) {
            LabelerDate labelerDate = new LabelerDate();
            labelerDate.setNumber(Integer.toString(i));
            labelerDates.add(labelerDate);

            if (i == 0 || i == 31) {
                labelerDate.setType(DateAdapter.VIEW_TYPE_PADDING);
            } else {
                labelerDate.setType(DateAdapter.VIEW_TYPE_ITEM);
            }
        }
    }
    /* this if most important, if expectedPositionDate < 0 recyclerView will return to nearest item*/

    private void calculatePositionAndScrollDate(RecyclerView recyclerView) {
        int expectedPositionDate = Math.round((allPixelsDate + paddingDate - firstItemWidthDate) / itemWidthDate);

        if (expectedPositionDate == -1) {
            expectedPositionDate = 0;
        } else if (expectedPositionDate >= recyclerView.getAdapter().getItemCount() - 2) {
            expectedPositionDate--;
        }
        scrollListToPositionDate(recyclerView, expectedPositionDate);

    }

    /* this if most important, if expectedPositionDate < 0 recyclerView will return to nearest item*/
    private void scrollListToPositionDate(RecyclerView recyclerView, int expectedPositionDate) {
        float targetScrollPosDate = expectedPositionDate * itemWidthDate + firstItemWidthDate - paddingDate;
        float missingPxDate = targetScrollPosDate - allPixelsDate;
        if (missingPxDate != 0) {
            recyclerView.smoothScrollBy((int) missingPxDate, 0);
        }
        setDateValue();
    }

    //
    private void setDateValue() {
        int expectedPositionDateColor = Math.round((allPixelsDate + paddingDate - firstItemWidthDate) / itemWidthDate);
        int setColorDate = expectedPositionDateColor + 1;
//        set color here
        dateAdapter.setSelecteditem(setColorDate);
    }

    public void ok(View view) {
        recyclerViewDate.smoothScrollToPosition(5);
    }


    public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
        private ArrayList<LabelerDate> dateDataList;


        private static final int VIEW_TYPE_PADDING = 1;
        private static final int VIEW_TYPE_ITEM = 2;
        private int paddingWidthDate = 0;

        private int selectedItem = -1;

        public DateAdapter(ArrayList<LabelerDate> dateData, int paddingWidthDate) {
            this.dateDataList = dateData;
            this.paddingWidthDate = paddingWidthDate;

        }


        @Override
        public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,
                        parent, false);
                return new DateViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,
                        parent, false);

                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                layoutParams.width = paddingWidthDate;
                view.setLayoutParams(layoutParams);
                return new DateViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(DateViewHolder holder, int position) {
            LabelerDate labelerDate = dateDataList.get(position);
            if (getItemViewType(position) == VIEW_TYPE_ITEM) {
                holder.tvDate.setText(labelerDate.getNumber());
                holder.tvDate.setVisibility(View.VISIBLE);

                Log.d("", "default " + position + ", selected " + selectedItem);
                if (position == selectedItem) {
                    Log.d("", "center" + position);
                    holder.tvDate.setTextColor(Color.parseColor("#76FF03"));
                    holder.tvDate.setTextSize(35);

                } else {
                    holder.tvDate.setTextColor(Color.BLACK);
                    holder.tvDate.setTextSize(18);
                }
            } else {
                holder.tvDate.setVisibility(View.INVISIBLE);
            }
        }

        public void setSelecteditem(int selecteditem) {
            this.selectedItem = selecteditem;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dateDataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            LabelerDate labelerDate = dateDataList.get(position);
            if (labelerDate.getType() == VIEW_TYPE_PADDING) {
                return VIEW_TYPE_PADDING;
            } else {
                return VIEW_TYPE_ITEM;
            }

        }


        public class DateViewHolder extends RecyclerView.ViewHolder {
            public TextView tvDate;

            public DateViewHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_year);
            }
        }
    }

    private class LabelerDate {
        private int type;
        private String number;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}