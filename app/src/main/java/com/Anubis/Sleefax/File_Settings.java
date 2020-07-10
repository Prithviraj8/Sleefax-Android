package com.Anubis.Sleefax;

import java.util.ArrayList;

public class File_Settings {

    public ArrayList<String> urls = new ArrayList<>();
    public ArrayList<String> fileTypes = new ArrayList<>();
    public ArrayList<String> colors = new ArrayList<>();
    public ArrayList<Integer> copies = new ArrayList<>();
    public ArrayList<String> pageSize = new ArrayList<>();
    public ArrayList<String> orientations = new ArrayList<>();
    public boolean bothSides[];
    public ArrayList<String> customPages = new ArrayList<>();
    public ArrayList<String> customValues = new ArrayList<>();
    public ArrayList<Integer> numberOfPages = new ArrayList<>();
    public ArrayList<String> fileNames = new ArrayList<>();
    public ArrayList<String> fileSizes = new ArrayList<>();
    public double[] pricePerFile;

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(ArrayList<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ArrayList<Integer> getCopies() {
        return copies;
    }

    public void setCopies(ArrayList<Integer> copies) {
        this.copies = copies;
    }

    public ArrayList<String> getPageSize() {
        return pageSize;
    }

    public void setPageSize(ArrayList<String> pageSize) {
        this.pageSize = pageSize;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public void setOrientations(ArrayList<String> orientations) {
        this.orientations = orientations;
    }

    public boolean[] getBothSides() {
        return bothSides;
    }

    public void setBothSides(boolean[] bothSides) {
        this.bothSides = bothSides;
    }

    public ArrayList<String> getCustomPages() {
        return customPages;
    }

    public void setCustomPages(ArrayList<String> customPages) {
        this.customPages = customPages;
    }

    public ArrayList<String> getCustomValues() {
        return customValues;
    }

    public void setCustomValues(ArrayList<String> customValues) {
        this.customValues = customValues;
    }

    public ArrayList<Integer> getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(ArrayList<Integer> numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public ArrayList<String> getFileSizes() {
        return fileSizes;
    }

    public void setFileSizes(ArrayList<String> fileSizes) {
        this.fileSizes = fileSizes;
    }

    public double[]  getPricePerFile() {
        return pricePerFile;
    }

    public void setPricePerFile(double[] pricePerFile) {
        this.pricePerFile = pricePerFile;
    }

    public File_Settings(ArrayList<String> urls, ArrayList<String> fileTypes, ArrayList<String> colors, ArrayList<Integer> copies, ArrayList<String> pageSize, ArrayList<String> orientations, boolean[] bothSides, ArrayList<String> customPages, ArrayList<String> customValues, ArrayList<Integer> numberOfPages, ArrayList<String> fileNames, ArrayList<String> fileSizes, double[] pricePerFile) {
        this.urls = urls;
        this.fileTypes = fileTypes;
        this.colors = colors;
        this.copies = copies;
        this.pageSize = pageSize;
        this.orientations = orientations;
        this.bothSides = bothSides;
        this.customPages = customPages;
        this.customValues = customValues;
        this.numberOfPages = numberOfPages;
        this.fileNames = fileNames;
        this.fileSizes = fileSizes;
        this.pricePerFile = pricePerFile;
    }
}
