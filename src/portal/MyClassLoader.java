package portal;

public class MyClassLoader extends ClassLoader{
    static int maxsize=1024000;
	public MyClassLoader()
	{
	}
    public Class load(String fileName,String className) throws java.lang.Exception
    {
            try{
                    Class myClass=this.findLoadedClass(className);
                    System.out.println(myClass.getName() + " is loaded!");
                    return myClass;
            }
            catch (Exception e) {
                    System.out.println("Loading class exception! "+e);
            }
            java.io.FileInputStream file = new java.io.FileInputStream(fileName);
            byte[] classByte=new byte[maxsize];
            int readSize;
            readSize = file.read(classByte);
            // For debug.
            // System.out.println(new String(classByte));
            file.close();
            return defineClass(className,classByte,0,readSize);
    }
}
