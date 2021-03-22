package com.nand_project.www.smartabsensi.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nand_project.www.smartabsensi.R;
import com.nand_project.www.smartabsensi.SessionManager.SessionManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;
import static android.text.TextUtils.split;


/**
 * Created by USER on 08-Dec-17.
 */

public class TabHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private EditText email, password;
    private Button btnLogin, btn_logout;
    private TextView tv_scan_qr, tv_nbi, tv_nama, tv_waktu;
    public String valueQR[];

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, database;

    String uniqueKey;

    SessionManager sessionManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Model> models =new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);

        tv_scan_qr = rootView.findViewById(R.id.scanQR);
        tv_nbi = rootView.findViewById(R.id.nbi);
        tv_nama = rootView.findViewById(R.id.username);
        tv_waktu = rootView.findViewById(R.id.waktu);
        btn_logout = rootView.findViewById(R.id.logout_cust);

// SwipeRefreshLayout
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        setHasOptionsMenu(true);

        sessionManager = new SessionManager(getActivity());
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (tv_nama.getText().equals("Silahkan absen dahulu")){
                        btn_logout.setVisibility(View.GONE);
                    }else {
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
                        final String date = df.format(Calendar.getInstance().getTime());

                        databaseReference.child(uniqueKey).setValue(new com.nand_project.www.smartabsensi.Admin.Data.Absen.Model(tv_nama.getText().toString(), tv_nbi.getText().toString(), tv_nama.getText().toString() + "#" + tv_nbi.getText().toString(), tv_waktu.getText().toString(), date, uniqueKey))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Berhasil logout pada pukul " + date, Toast.LENGTH_SHORT).show();
                                        onRefresh();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        sessionManager.logout();
                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Silahkan refresh dengan menswipe layar", Toast.LENGTH_SHORT).show();
                }


            }
        });

        tv_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),QRCode.class);
                startActivityForResult(intent,1);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Absen_NAND");
        database = FirebaseDatabase.getInstance().getReference();

        checkLogin();

        return rootView;
    }

    private void checkLogin() {
        mSwipeRefreshLayout.setRefreshing(true);

        if (sessionManager.isLogin()){



            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    uniqueKey = sessionManager.getSessionData().get("ID");
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else {
            tv_nbi.setText("Silahkan absen dahulu");
            tv_nama.setText("Silahkan absen dahulu");
            tv_waktu.setText("Silahkan absen dahulu");
            btn_logout.setVisibility(View.GONE);

        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

            //jika uniquekey sama dengan getkey maka tampilkan
            if (childDataSnapshot.getKey().equals(uniqueKey)){
                tv_nama.setText(childDataSnapshot.child("nama").getValue().toString());
                tv_nbi.setText(childDataSnapshot.child("nbi").getValue().toString());
                tv_waktu.setText(childDataSnapshot.child("masuk").getValue().toString());
            }
//            Log.v("key",""+ childDataSnapshot.getKey()); //displays the key for the node
//            Log.v("nama",""+ childDataSnapshot.child("nama").getValue());   //gives the value for given keyname
            //Log.v("child",uniqueKey);

            btn_logout.setVisibility(View.VISIBLE);


        }



    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                try{
                    final String[] strEditText = split(data.getStringExtra("hasil"),"#");

                    database.child("User").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("snap",dataSnapshot.toString());
                            /**
                             * Saat ada data baru, masukkan datanya ke ArrayList
                             */
                            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                /**
                                 * Mapping data pada DataSnapshot ke dalam object Barang
                                 * Dan juga menyimpan primary key pada object Barang
                                 * untuk keperluan Edit dan Delete data
                                 */
                                Model model = noteDataSnapshot.getValue(Model.class);
                                model.setKey(noteDataSnapshot.getKey());

                                /**
                                 * Menambahkan object Barang yang sudah dimapping
                                 * ke dalam ArrayList
                                 */
                                models.add(model);
                            }
                            for (int i = 0; i<models.size();i++){
                                //Toast.makeText(getActivity(), models.get(i).getNama()+" "+models.get(i).getNbi(), Toast.LENGTH_SHORT).show();
                                if (strEditText[0].equals(models.get(i).getNama()) && strEditText[1].equals(models.get(i).getNbi())){
                                    Toast.makeText(getActivity(), "Absen masuk berhasil", Toast.LENGTH_SHORT).show();

                                    tv_nama.setText(strEditText[0]);
                                    tv_nbi.setText(strEditText[1]);

                                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
                                    String date = df.format(Calendar.getInstance().getTime());

                                    tv_waktu.setText(date);



                                    inputData(strEditText[0],strEditText[1],data.getStringExtra("hasil"),date,"");

                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the initial value and again
                                            // whenever data at this location is updated.
                                            showData(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                }else {
                                    Toast.makeText(getActivity(), "Data tidak ada dalam database, silahkan hubungi administrator", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
                        }

                    });

                }catch (Exception e){
                    Intent intent = new Intent(getActivity(),QRCode.class);
                    startActivityForResult(intent,1);
                }
            }
            if(resultCode == RESULT_FIRST_USER) {
                Toast.makeText(getActivity(), "QR Kode tidak sesuai format", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),QRCode.class);
                startActivityForResult(intent,1);
            }
        }
    }


    private void inputData(String nama, String nbi, String qr, String masuk, String keluar) {
        com.nand_project.www.smartabsensi.Admin.Data.Absen.Model model = new com.nand_project.www.smartabsensi.Admin.Data.Absen.Model();
        model.setNama(nama);
        model.setNbi(nbi);
        model.setQr(qr);
        model.setMasuk(masuk);
        model.setKeluar(keluar);

        databaseReference.push().setValue(model, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                sessionManager.createSession(databaseReference.getKey());
            }
        });


    }

    public void createPDF(){
//reference to EditText
//create document object
        Document doc=new Document();
//output file path
        String outpath=Environment.getExternalStorageDirectory()+"/laporan absensi "+tv_nama.getText()+".pdf";
        try {
//create pdf writer instance
            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
//open the document for writing
            doc.open();
//add paragraph to the document
            doc.add(new Paragraph("==================="));
            doc.add(new Paragraph("SMART ABSENSI"));
            doc.add(new Paragraph("==================="));
            doc.add(new Paragraph("Nama : "+tv_nama.getText()));
            doc.add(new Paragraph("NBI : "+tv_nbi.getText()));
            doc.add(new Paragraph("Waktu Absen : "+tv_waktu.getText()));

            doc.add(new Paragraph("\n\n================================\n"));
            doc.add(new Paragraph(" dibuat oleh http://www.nand-project.com"));
//close the document
            doc.close();
            Toast.makeText(getActivity(), "Laporan berhasil dibuat", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_save:
                if (sessionManager.isLogin()) {
                    createPDF();
                    Toast.makeText(getActivity(), "Save as PDF berhasil", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getActivity(), "Silahkan absen terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getActivity(), "Fitur ini masih coming soon", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        checkLogin();
    }
}
