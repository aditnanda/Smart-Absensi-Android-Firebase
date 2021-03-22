package com.nand_project.www.smartabsensi.Home;

public class Model {
    String nama;
    String nbi;
    String key;

    public Model(){

    }

    public Model(String nama, String nbi) {
        this.nama = nama;
        this.nbi = nbi;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Model{" +
                "nama='" + nama + '\'' +
                ", nbi='" + nbi + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
