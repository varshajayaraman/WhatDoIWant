package com.example.Project;
import java.util.*;
import java.util.stream.Collectors;

public class Sequence {

    private List<List<Integer>> elements;
    private Double support;
    private int count;

    public Sequence(List<List<Integer>> elements) {
        this.elements = elements;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void incrementCount(){
        this.count++;
    }

    public Sequence() {
    }

    public List<List<Integer>> getElements() {
        return elements;
    }

    public void setElements(List<List<Integer>> elements) {
        this.elements = elements;
    }

    public Double getSupport() {
        return support;
    }

    public void setSupport(Double support) {
        this.support = support;
    }

    //Sequence Copy
    public Sequence clone() {
        List<List<Integer>> newElementList = new ArrayList<>();
        if(elements == null) {
            return null;
        }
        for(List<Integer> element : elements) {
            List<Integer> newElement = new ArrayList<>();
            newElement.addAll(element);
            newElementList.add(newElement);
        }
        Sequence s = new Sequence(newElementList);
        return s;
    }

    public boolean isItemPresent(Integer item) {
        for (List<Integer> element : elements) {
            if(element.contains(item))
                return true;
        }
        return false;
    }

    public Integer getFirstItem() {
        return elements.get(0).get(0);
    }

    public Integer getLastItem() {
        List<Integer> itemSet =  elements.get(elements.size()-1);
        return itemSet.get(itemSet.size()-1);
    }

    public List<Integer> getItemSet() {
        Set<Integer> itemSet = elements.stream().flatMap(List::stream).collect(Collectors.toSet());
        return new ArrayList<>(itemSet);
    }

    public Integer getLength() {
        return elements.stream().flatMap(List::stream).collect(Collectors.toList()).size();
    }

    public Integer getSize() {
        return elements.size();
    }

    public List<Integer> getLastElement() {
        return elements.get(elements.size() -1);
    }

    public List<Integer> getFirstElement() {
        return elements.get(0);
    }

    public Sequence removeFirstItem(boolean isReverse) {
        Sequence filteredSeq = this.clone();
        if(!isReverse) {
            List<Integer> itemsSet = elements.get(0);
            if (itemsSet.size() == 1) {
                filteredSeq.getElements().remove(0);
            } else {
                filteredSeq.getElements().get(0).remove(0);
            }
        } else {
            List<Integer> itemsSet = elements.get(elements.size()-1);
            if (itemsSet.size() == 1) {
                filteredSeq.getElements().remove(elements.size()-1);
            } else {
                filteredSeq.getElements().get(elements.size()-1).remove(itemsSet.size() -1);
            }
        }
        return filteredSeq;
    }

    public Sequence removeSecondItem(boolean isReverse) {
        Sequence filteredSeq = this.clone();
        if(!isReverse) {
            List<Integer> itemsSet = elements.get(0);
            if (itemsSet.size() >1) {
                filteredSeq.getElements().get(0).remove(1);
            } else {
                itemsSet = elements.get(1);
                if(itemsSet.size() == 1) {
                    filteredSeq.getElements().remove(1);
                }else {
                    filteredSeq.getElements().get(1).remove(0);
                }
            }
        } else {
            List<Integer> itemsSet = elements.get(elements.size()-1);
            if (itemsSet.size() > 1) {
                filteredSeq.getElements().get(0).remove(itemsSet.size()-2);
            } else {
                itemsSet = elements.get(elements.size() -2);
                if(itemsSet.size() == 1) {
                    filteredSeq.getElements().remove(elements.size()-2);
                }else {
                    filteredSeq.getElements().get(elements.size() - 2).remove(itemsSet.size() - 1);
                }
            }
        }
        return filteredSeq;

    }


    public boolean equals(Sequence s) {
        if(elements.size() != s.getSize()) {
            return false;
        }
        for (int i = 0; i < s.getSize(); i++) {
            if(elements.get(i).size() != s.getElements().get(i).size()) {
                return false;
            }
            for (int j = 0; j < s.getElements().get(i).size(); j++) {
                if(elements.get(i).get(j) != s.getElements().get(i).get(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    //Checking subsequence
    public boolean subsequence(Sequence s) {
        int i = 0;
        while(i < elements.size()) {
            for (int j = 0; j < s.getSize(); j++) {
                int prevL = 0, k;
                List<Integer> L_elements = s.getElements().get(j);

                for (k =0; k < elements.get(i).size(); k++) {
                    L_elements = L_elements.subList(prevL,L_elements.size());
                    int index = L_elements.indexOf(elements.get(i).get(k));
                    if(index == -1) {
                        break;
                    }
                    prevL = index + 1;
                }
                if(k == elements.get(i).size()) {
                    i++;
                }
                if(i == elements.size()) break;
            }
            if(i != elements.size())
                break;
        }
        return  i == this.getSize();
    }

    public  List<Sequence> getAll_K_1_Subsequence() {
        List<Sequence> subsequenceList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            if(elements.get(i).size() == 1 ) {
                // new subsequence
                List<List<Integer>> subsequenceElements = new ArrayList<>(elements);
                subsequenceElements.remove(i);
                Sequence subsequence = new Sequence(subsequenceElements);
                subsequenceList.add(subsequence);
                continue;
            }
            List<List<Integer>> subsequenceElements = new ArrayList<>(elements.subList(0,i));
            for (int j = 0; j < elements.get(i).size(); j++) {
                List<Integer> tempList = new ArrayList<>( elements.get(i));
                tempList.remove(j);
                subsequenceElements.add(tempList);
                subsequenceElements.addAll(elements.subList(i+1, elements.size()));
                Sequence subsequence = new Sequence(subsequenceElements);
                subsequenceList.add(subsequence);
            }
        }
        return subsequenceList;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("<");
        for (List<Integer> element :elements) {
            output.append(element.toString());
        }
        output.append("> count: " + getCount());
        return output.toString().replace("[", "{").replace("]","}");
    }

    //Sorting items in each element based on MIS
    public void  sortEachElement(Map<Integer, Double> misMap) {
        for(List<Integer> element : elements) {
            Collections.sort(element, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return Double.compare(misMap.get(o1), misMap.get(o2));
                }
            });
        }
    }
}
