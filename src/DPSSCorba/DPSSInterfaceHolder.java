package DPSSCorba;

/**
* DPSSCorba/DPSSInterfaceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DPSSCorba.idl
* Saturday, June 20, 2020 7:59:49 o'clock PM EDT
*/

public final class DPSSInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public DPSSCorba.DPSSInterface value = null;

  public DPSSInterfaceHolder ()
  {
  }

  public DPSSInterfaceHolder (DPSSCorba.DPSSInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DPSSCorba.DPSSInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DPSSCorba.DPSSInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DPSSCorba.DPSSInterfaceHelper.type ();
  }

}
