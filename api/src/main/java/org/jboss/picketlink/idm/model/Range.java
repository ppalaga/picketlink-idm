package org.jboss.picketlink.idm.model;

/**
 * Represent range in paginated query
 */
public class Range
{
    //TODO: Just a quick impl

    private int offset;

    private int limit = -1;

    private Range() 
    {
    }

    private Range(int offset, int limit) 
    {
        this.offset = offset;
        this.limit = limit;
    }

    int getPage() 
    {
        //TODO: Calculate based on limit/offset.
        //TODO: Should it start from 0 or 1? Rather 1....
        return 1;
    }

    public int getOffset() 
    {
        return offset;
    }

    public int getLimit() 
    {
        return limit;
    }

    public Range of(int offset, int limit) 
    {
        return new Range(offset, limit);
    }

    public Range next() 
    {
        offset += limit;
        return this;
    }

}
