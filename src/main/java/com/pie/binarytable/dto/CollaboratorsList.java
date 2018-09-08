package com.pie.binarytable.dto;

import java.util.ArrayList;

public class CollaboratorsList
{
    ArrayList<String> collaborators;

    public CollaboratorsList()
    {
        collaborators = new ArrayList<>();
    }

    public ArrayList<String> getCollaborators() {
        return collaborators;
    }

    public void addCollaborator(String col)
    {
        collaborators.add(col);
    }
}
