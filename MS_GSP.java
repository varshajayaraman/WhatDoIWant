package com.example.Project;

import java.util.*;
import java.util.stream.Collectors;

public class MS_GSP {

    private Map<Integer, Double> misMap;
    private Double sdc;
    private List<Sequence> dataSequenceList;
    private Integer dataSize;
    public MS_GSP(Map<Integer, Double> misMap, Double sdc, List<Sequence> dataSequenceList) {
        this.misMap = misMap;
        this.sdc = sdc;
        this.dataSequenceList = dataSequenceList;
        this.dataSize = dataSequenceList.size();
    }

    public Map<Integer, List<Sequence>> execute() {

        Map<Integer, List<Sequence>> frequentSequenceMap = new HashMap<>();
        List<Integer> itemSet = new ArrayList<>( misMap.keySet() );
        Collections.sort(itemSet, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Double.compare(misMap.get(o1), misMap.get(o2));
            }
        });

        Map<Integer, Integer> L_sequences =(LinkedHashMap) init_pass(itemSet);
        frequentSequenceMap.put(1,get_1_Sequence(L_sequences));

        int k = 2;
        while (true) {
            List<Sequence> candidateSeq = null;
            if(k ==2 ) {
                candidateSeq = L2CandidateGenSpm(L_sequences);
            }else {
                candidateSeq = MSCandidateGenSpm(frequentSequenceMap.get(k-1));
            }
            List<Sequence> c_MISFiltered = new ArrayList<>();
            for (Sequence dataSeq : dataSequenceList) {
                for (Sequence candidate : candidateSeq) {
                    if(candidate.subsequence(dataSeq)) {
                        candidate.incrementCount();
                    }
                    Sequence c2 = findCWithoutMinMISItem(candidate);
                    if(c2.subsequence(dataSeq)) {
                        c2.incrementCount();
                        c_MISFiltered.add(c2);
                    }
                }
            }
            List<Sequence> fkSequenceList = new ArrayList<>();
            for (Sequence candidate : candidateSeq) {
                if(candidate.getCount().doubleValue()/ dataSize >= getLeastMISvalue(candidate.getItemSet())) {
                    fkSequenceList.add(candidate);
                }
            }
            if(fkSequenceList.isEmpty())   {
                break;
            }
            frequentSequenceMap.put(k, fkSequenceList);
            k++;
        }
        return  frequentSequenceMap;
    }

    //Eliminating the item with lowest MIS
    private Sequence findCWithoutMinMISItem(Sequence candidate) {
        Double minMisValue = getLeastMISvalue(candidate.getItemSet());
        Sequence c2 = candidate.clone();
        Iterator<List<Integer>> itemSetIterator = c2.getElements().iterator();
        while (itemSetIterator.hasNext()) {
            List<Integer> element = itemSetIterator.next();
            Iterator<Integer> itemIterator = element.iterator();
            while (itemIterator.hasNext()) {
                Integer item = itemIterator.next();
                if (minMisValue.doubleValue() == misMap.get(item)) {
                    itemIterator.remove();
                    break;
                }
            }
            if (element.isEmpty()) {
                itemSetIterator.remove();
            }
        }
        return c2;
    }

    private Double getLeastMISvalue(List<Integer> itemSet) {
        return itemSet.stream().map(item -> misMap.get(item)).min(Comparator.comparing(Double::valueOf)).get();
    }

    //Adding to candidate during JOIN step
    private void addIfNotExists(List<Sequence> candidateSequence, Sequence c1) {
        c1.sortEachElement(misMap);
        for(Sequence candidate : candidateSequence) {
            if(candidate.equals(c1))
                return;
        }
        candidateSequence.add(c1);
    }

    private List<Sequence> MSCandidateGenSpm(List<Sequence> previousFSequence) {

        List<Sequence> candidateSequence = new ArrayList<>();
        for ( int i = 0; i< previousFSequence.size();i++) {
            for (int j = 0; j < previousFSequence.size(); j++) {
                Sequence s1 = previousFSequence.get(i);
                Sequence s2 = previousFSequence.get(j);
                if(misMap.get(s1.getFirstItem()) < getLeastMISvalue(s1.removeFirstItem(false).getItemSet())) {
                    if(s1.removeSecondItem(false).equals(s2.removeFirstItem(true))
                            && misMap.get(s2.getLastItem()) > misMap.get(s1.getFirstItem()) ) {
                        if(s2.getLastElement().size() == 1) {
                            Sequence c1 =  s1.clone();
                            c1.getElements().add(s2.getLastElement());

                            addIfNotExists(candidateSequence, c1);
                            // MIS
                            if(s1.getLength() == 2 && s1.getSize() == 2
                                    && misMap.get(s2.getLastItem()) > misMap.get(s1.getLastItem())) {
                                Sequence c2 = s1.clone();
                                c2.getLastElement().add(s2.getLastItem());

                                addIfNotExists(candidateSequence, c2);
                            }
                        } else if((s1.getLength() == 2 && s1.getSize() == 1
                                && misMap.get(s2.getLastItem()) > misMap.get(s1.getLastItem()))
                                || s1.getLength() > 2 ) {
                            Sequence c2 = s1.clone();
                            c2.getLastElement().add(s2.getLastItem());

                            addIfNotExists(candidateSequence, c2);
                        }
                    }
                }
                else if(misMap.get(s2.getLastItem()) < getLeastMISvalue(s2.removeFirstItem(true).getItemSet())) {
                    // candidates are produced by extending s2 with first item of s1.
                    if(s2.removeSecondItem(true).equals(s1.removeFirstItem(false))
                            &&  misMap.get(s1.getFirstItem()) > misMap.get(s2.getLastItem()) ) {
                        if(s1.getFirstElement().size() == 1) {
                            Sequence c1 = s2.clone();
                            c1.getElements().add(0, s1.getFirstElement());

                            addIfNotExists(candidateSequence, c1);
                            if(s2.getLength() == 2 && s2.getSize() == 2
                                    && misMap.get(s1.getFirstItem()) > misMap.get(s2.getFirstItem())) {
                                Sequence c2 = s2.clone();
                                c2.getFirstElement().add(0,s1.getFirstItem());

                                addIfNotExists(candidateSequence, c2);
                            }
                        } else if((s2.getLength() == 2 && s2.getSize() == 1
                                && misMap.get(s1.getFirstItem()) > misMap.get(s2.getFirstItem()))
                                || s2.getLength() > 2 ) {
                            Sequence c1 = s2.clone();
                            c1.getFirstElement().add(0, s1.getFirstItem());

                            addIfNotExists(candidateSequence, c1);
                        }
                    }
                } else {
                    if(s1.removeFirstItem(false).equals(s2.removeFirstItem(true))) {
                        if(s2.getLastElement().size() == 1) {
                            Sequence c1 = s1.clone();
                            c1.getElements().add(0, s2.getLastElement());

                            addIfNotExists(candidateSequence, c1);
                        }
                        if(s2.getLastElement().size() > 1) {
                            Sequence c1 = s1.clone();
                            c1.getLastElement().add( s2.getLastItem());

                            addIfNotExists(candidateSequence, c1);
                        }
                    }
                }

                //PRUNE Step
                Iterator<Sequence> seqIt = candidateSequence.iterator();
                while (seqIt.hasNext()){
                    Sequence candidate = seqIt.next();

                    double diff = getMaxMinDiff(candidate.getItemSet());
                    if(diff > sdc) {
                        seqIt.remove();
                        continue;
                    }

                    List<Sequence> c_subsequenceList = candidate.getAll_K_1_Subsequence();
                    for( Sequence subsequence : c_subsequenceList) {
                        if(getLeastMISvalue(subsequence.getItemSet()).intValue() == getLeastMISvalue(candidate.getItemSet())) {
                            if(previousFSequence.contains(subsequence)) {
                                seqIt.remove();
                            }
                        }
                    }
                }
            }
        }
        return candidateSequence;
    }

    //Getting Support Difference
    private double getMaxMinDiff(List<Integer> items) {
        Double minVal = 2.0;
        Double maxVal = -1.0;
        Map<Integer, Integer> itemCountMap = new HashMap<>();
        for(Sequence sequence : dataSequenceList) {
            for (Integer item : items) {
                if(sequence.isItemPresent(item)) {
                    if(!itemCountMap.containsKey(item)) {
                        itemCountMap.put(item, 0);
                    }
                    itemCountMap.put(item, itemCountMap.get(item) + 1);
                }
            }
        }
        for (Integer item : items) {
            Double sup = itemCountMap.get(item)/dataSize.doubleValue();
            minVal = minVal > sup  ? sup : minVal;
            maxVal = maxVal < sup ? sup : maxVal;
        }
        return maxVal - minVal ;

    }

    private List<Sequence> L2CandidateGenSpm(Map<Integer, Integer> L_sequences) {
        List<Sequence> candidateSequenceList = new ArrayList<>();
        List<Integer> LitemList = new ArrayList<>(L_sequences.keySet());
        for(int i =0; i< L_sequences.size();i++) {
            if(L_sequences.get(LitemList.get(i)) >= misMap.get(LitemList.get(i))) {
                for(int h = 0; h<LitemList.size();h++) {
                    Double l_sup =  L_sequences.get(LitemList.get(i)).doubleValue()/ dataSize;
                    Double h_sup =  L_sequences.get(LitemList.get(h)).doubleValue()/ dataSize;
                    if(L_sequences.get(LitemList.get(h)) >= misMap.get(LitemList.get(i))
                            && Math.abs(h_sup - l_sup) <= sdc) {
                        Sequence s = new Sequence(Arrays.asList(Arrays.asList(LitemList.get(i), LitemList.get(h))));
                        candidateSequenceList.add(s);
                        candidateSequenceList.add(new Sequence(Arrays.asList(Arrays.asList(LitemList.get(i)),
                                Arrays.asList(LitemList.get(h)))));
                    }
                }
            }
        }
        return candidateSequenceList;
    }

    private List<Sequence> get_1_Sequence(Map<Integer, Integer> L_sequences) {
        List<Integer> f1_sequence =  L_sequences.entrySet().stream().filter(itemCountEntry ->
                itemCountEntry.getValue()/dataSize.doubleValue() >= misMap.get(itemCountEntry.getKey()) )
                .map(Map.Entry::getKey).collect(Collectors.toList());
        List<Sequence> sequenceList = new ArrayList<>();
        for(Integer item : f1_sequence) {
            Sequence s = new Sequence(Arrays.asList(Arrays.asList(item)));
            s.setCount(L_sequences.get(item));
            sequenceList.add(s);
        }
        return sequenceList;
    }

    //Initial screening through data
    private  Map<Integer, Integer> init_pass(List<Integer> itemSet) {
        Map<Integer, Integer> itemCountMap = new HashMap<>();
        for(Sequence sequence : dataSequenceList) {
            for(Integer item: itemSet) {
                if(sequence.isItemPresent(item)) {
                    if(!itemCountMap.containsKey(item)) {
                        itemCountMap.put(item, 0);
                    }
                    itemCountMap.put(item, itemCountMap.get(item) + 1);
                }
            }
        }
        Double constantMIS = null;
        Map<Integer, Integer> filteredItemCountMap = new LinkedHashMap<>();
        for(Integer item : itemSet) {
            double mis = constantMIS == null? misMap.get(item) : constantMIS;
            Integer count = itemCountMap.get(item);
            count = count == null ? 0 : count;
            if(count.doubleValue()/dataSize >= mis) {
                constantMIS = mis;
                filteredItemCountMap.put(item, itemCountMap.get(item));
            }
        }
        return filteredItemCountMap;
    }
}
