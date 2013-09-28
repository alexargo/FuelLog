package com.atoennis.fuellog;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.atoennis.fuellog.domain.Trip;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that contain this fragment
 * must implement the {@link TripFormFragment.OnTripFormInteractionListener} interface to handle
 * interaction events. Use the {@link TripFormFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class TripFormFragment extends Fragment
{
    private static final String           EXTRA_TRIP = "EXTRA_TRIP";
    private OnTripFormInteractionListener listener;
    private Button                        datePicker;
    private EditText                      distanceInput;
    private EditText                      volumeInput;
    private EditText                      volumePriceInput;

    private Trip                          trip;

    /**
     * Use this factory method to create a new instance of this fragment
     * 
     * @return A new instance of fragment TripFormFragment.
     */
    public static TripFormFragment newInstance()
    {
        return new TripFormFragment();
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     * 
     * @param Trip trip used to populate the fields of the form
     * @return A new instance of fragment TripFormFragment.
     */
    public static TripFormFragment newInstance(Trip trip)
    {
        if (trip == null)
        {
            return newInstance();
        }

        TripFormFragment fragment = new TripFormFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    public TripFormFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        trip = new Trip();
        if (getArguments() != null)
        {
            trip = (Trip) getArguments().getSerializable(EXTRA_TRIP);
        }

        if (savedInstanceState != null)
        {
            trip = (Trip) savedInstanceState.getSerializable(EXTRA_TRIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trip_form, container, false);
        distanceInput = (EditText) view.findViewById(R.id.distance);
        volumeInput = (EditText) view.findViewById(R.id.volume);
        volumePriceInput = (EditText) view.findViewById(R.id.volume_price);
        datePicker = (Button) view.findViewById(R.id.date_picker);

        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onDateSelectorPressed(trip.date);
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        if (trip != null)
        {
            cal.setTime(trip.date);

            if (trip.distance != null)
            {
                distanceInput.setText(String.format("%,d", trip.distance));
            }
            if (trip.volume != null)
            {
                volumeInput.setText(Double.toString(trip.volume));
            }
            if (trip.volumePrice != null)
            {
                volumePriceInput.setText(Double.toString(trip.volumePrice));
            }
        }
        setDateLabel(new Date(cal.getTimeInMillis()));

        distanceInput.addTextChangedListener(new FormatTextWatcher(distanceInput));

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (OnTripFormInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(String.format("%s must implement %s", activity.toString(),
                OnTripFormInteractionListener.class.getSimpleName()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        trip = getFormData();
        outState.putSerializable(EXTRA_TRIP, trip);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    public void onDateSelected(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, monthOfYear, dayOfMonth);

        trip = getFormData();
        setDateLabel(new Date(cal.getTimeInMillis()));
    }

    public boolean isFormValid()
    {
        String distance = distanceInput.getText().toString();
        String volume = volumeInput.getText().toString();
        String volumePrice = volumePriceInput.getText().toString();

        return distance != null
            && !distance.isEmpty() && volume != null && !volume.isEmpty() && volumePrice != null
            && !volumePrice.isEmpty();
    }

    public Trip getFormData()
    {
        String dateStr = datePicker.getText().toString();
        Date date = null;
        Integer distance = null;
        Double volume = null;
        Double volumePrice = null;
        try
        {
            date = new SimpleDateFormat("EE, MMM d, yyyy").parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (!distanceInput.getText().toString().isEmpty())
        {
            String formatted = distanceInput.getText().toString().replace(",", "");
            distance = Integer.parseInt(formatted);
        }
        if (!volumeInput.getText().toString().isEmpty())
        {
            volume = Double.parseDouble(volumeInput.getText().toString());
        }
        if (!volumePriceInput.getText().toString().isEmpty())
        {
            volumePrice = Double.parseDouble(volumePriceInput.getText().toString());
        }

        return new Trip(trip.id, date, distance, volume, volumePrice);
    }

    private void setDateLabel(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, MMM d, yyyy");
        String formattedDate = dateFormat.format(date);

        datePicker.setText(formattedDate);
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating
     * with Other Fragments</a> for more information.
     */
    public interface OnTripFormInteractionListener
    {
        public void onDateSelectorPressed(Date date);
    }

    private class FormatTextWatcher implements TextWatcher
    {
        private final WeakReference<EditText> editTextRef;
        private boolean                       isInAfterTextChanged = false;

        public FormatTextWatcher(EditText editText)
        {
            this.editTextRef = new WeakReference<EditText>(editText);
        }

        @Override
        public synchronized void afterTextChanged(Editable s)
        {
            if (!isInAfterTextChanged)
            {
                isInAfterTextChanged = true;
                EditText editText = editTextRef.get();
                if (editText != null
                    && s.length() > 0)
                {
                    String formatted = s.toString().replace(",", "");
                    formatted = String.format("%,d", Integer.valueOf(formatted));

                    editText.setText(formatted);
                    editText.setSelection(editText.getText().toString().length());
                }

                isInAfterTextChanged = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

    }
}
