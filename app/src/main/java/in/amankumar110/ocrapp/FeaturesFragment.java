package in.amankumar110.ocrapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import in.amankumar110.ocrapp.databinding.FeatureItemLayoutBinding;
import in.amankumar110.ocrapp.databinding.FragmentFeaturesBinding;
import in.amankumar110.ocrapp.enums.FeatureType;
import in.amankumar110.ocrapp.view.adapters.FeaturesAdapter;
import in.amankumar110.ocrapp.view.utils.GridLayoutDecorator;

public class FeaturesFragment extends Fragment {


    private FeaturesAdapter featuresAdapter;
    private FragmentFeaturesBinding fragmentFeaturesBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and bind it to the binding
        View view = inflater.inflate(R.layout.fragment_features, container, false);
        fragmentFeaturesBinding = FragmentFeaturesBinding.bind(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        featuresAdapter = new FeaturesAdapter(requireContext(),onFeatureItemClicked);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position== FeatureType.FEATURE_COUNT-1) ? 2 : 1;
            }
        });
        fragmentFeaturesBinding.rvFeaturesList.addItemDecoration(new GridLayoutDecorator(20,20));
        fragmentFeaturesBinding.rvFeaturesList.setLayoutManager(gridLayoutManager);
        fragmentFeaturesBinding.rvFeaturesList.setAdapter(featuresAdapter);
    }


    private final View.OnClickListener onFeatureItemClicked = v -> {

        FeatureItemLayoutBinding binding = FeatureItemLayoutBinding.bind(v);
        String title = binding.tvFeatureTitle.getText().toString().trim();
        String upcomingFeatureTitle = requireContext().getString(R.string.upcoming_features_title);

        Class<? extends Fragment> fragmentClass = FeatureType.getFragmentForTitle(requireContext(),title);


        try {
            if(!title.equals(upcomingFeatureTitle) && fragmentClass!=null ) {
                Fragment fragment = fragmentClass.newInstance();
                getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fragment).commit();

            }

        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            throw new RuntimeException(e);
        }

    };
}