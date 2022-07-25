package com.etiennelawlor.moviehub.presentation.models;

import java.util.Date;
import java.util.List;

/**
 * Created by etiennelawlor on 2/20/17.
 */

public class PersonsPresentationModel {

    // region Fields
    private List<PersonPresentationModel> persons;
    private int pageNumber;
    private boolean isLastPage;
    private Date expiredAt;
    // endregion

    // region Constructors

    public PersonsPresentationModel() {
    }

    // endregion

    // region Getters

    public List<PersonPresentationModel> getPersons() {
        return persons;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    // endregion

    // region Setters

    public void setPersons(List<PersonPresentationModel> persons) {
        this.persons = persons;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    // endregion

    // Helper Methods
    public boolean hasPersons() { return persons.size() > 0;}

    public void incrementPageNumber() { this.pageNumber += 1; }
    // endregion

    @Override
    public String toString() {
        return "PersonsPresentationModel{" +
                "persons=" + persons +
                ", pageNumber=" + pageNumber +
                ", isLastPage=" + isLastPage +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
