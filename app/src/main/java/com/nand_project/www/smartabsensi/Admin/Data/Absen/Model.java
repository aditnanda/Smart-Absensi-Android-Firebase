package com.nand_project.www.smartabsensi.Admin.Data.Absen;

public class Model {
    String nama;
    String nbi;
    String qr;
    String masuk;
    String keluar;
    String key;

    public Model (){

    }

    public Model(String nama, String nbi, String qr, String masuk, String keluar, String key) {
        this.nama = nama;
        this.nbi = nbi;
        this.qr = qr;
        this.masuk = masuk;
        this.keluar = keluar;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNbi() {
        return nbi;
    }

    public void setNbi(String nbi) {
        this.nbi = nbi;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getMasuk() {
        return masuk;
    }

    public void setMasuk(String masuk) {
        this.masuk = masuk;
    }

    public String getKeluar() {
        return keluar;
    }

    public void setKeluar(String keluar) {
        this.keluar = keluar;
    }

    @Override
    public String toString() {
        return "Model{" +
                "nama='" + nama + '\'' +
                ", nbi='" + nbi + '\'' +
                ", qr='" + qr + '\'' +
                ", masuk='" + masuk + '\'' +
                ", keluar='" + keluar + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
