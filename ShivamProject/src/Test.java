import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
 
 
public class Test 
{
        static String imagePath[];
        static int clas=1,size;
        static FileOutputStream fostest,fospath;
        static File filetest,filepath;
        static PrintStream pstest,pspath;
        
public static void ImageTest(String[] imgName) throws IOException        
{
            PrintStream p =System.out;
            filetest = new File("src\\Data\\test.txt");
            filepath = new File("src\\Data\\test1.txt");
            fostest = new FileOutputStream(filetest);
            fospath = new FileOutputStream(filepath);
            
            pstest = new PrintStream(fostest);
            pspath = new PrintStream(fospath);
            System.setOut(pstest);
            size=imgName.length;
            
            imagePath=new String[imgName.length];

            for(int i=0;i<imgName.length;i++)
            {
                      File f = new File(imgName[i]);
                      if(f.isFile())
                      {
                          imagePath[i]=f.getAbsolutePath().toString();
                          ReadImage(imagePath[i]);
                      }
                      else
                      {
                          File[] f1=f.listFiles();
                          imagePath= new String[f1.length];
                          for(i=0;i<f1.length;i++)
                          {
                              imagePath[i]=f1[i].getAbsolutePath().toString();
                              ReadImage(imagePath[i]);

                          }        
                      }
            }
            System.setOut(pspath);
            for(int q=0;q<imagePath.length;q++)
                System.out.println(imagePath[q]);
            //System.out.println("");
}
                  
	public static void ReadImage(String imgName) throws IOException
        {
            
            int i,j; 
            int r1,g1,b1;
            BufferedImage bi = null;
            //System.out.println(imgName);
            try{
                
                        bi = ImageIO.read(new File(imgName));                                           
                        int[] rgb= new int[bi.getWidth()*bi.getHeight()];
                        int x=bi.getHeight();
                        int y=bi.getWidth();
                        int m=0,n,p;              
             
                        for( n=1;n<y;n++) 
                        {
                            for( p=1;p<x;p++)
                            {
                                    rgb[m++] = (bi.getRGB(n,p));
                            }
                        }
                        calculateIntensityHistogram(rgb,y,x);
                    }
                                         
               catch (IOException e) 
               {
                   System.out.println("Error : "+e);
               }
      
    }
    static public void calculateIntensityHistogram(int[] rgb, int width, int height)
    {
        int histogram1[]=new int[256];
        int histogram2[]=new int[256];
        int histogram3[]=new int[256];
        long sum=0;
        float avg[]=new float[16];
        for (int bin = 0; bin < 256; bin++)
        {
            histogram1[bin] = 0;
            histogram2[bin]=0;
            histogram3[bin]=0;
        } 
        
        
        for (int pix = 0; pix < width*height; pix += 3)
        {
              int pixVal = (rgb[pix] >> 16) & 0xff;
              histogram1[ pixVal ]++;                          
        } 
                    
        for (int pix = 0; pix < width*height; pix += 3)
        {
             int pixVal = (rgb[pix] >> 8) & 0xff;
             histogram2[ pixVal ]++;
        } 
        
        for (int pix = 0; pix < width*height; pix += 3)
        {
             int pixVal = rgb[pix] & 0xff;
             histogram3[ pixVal ]++;
        } 
        int n=0;
        float val[]=new float[20];
            
        val=calavg(histogram1);
        for(int i=0;i<4;i++)
                avg[n++]=val[i];
            
        val=calavg(histogram2);
        for(int i=0;i<4;i++)
                avg[n++]=val[i];
            
        val=calavg(histogram3);
        for(int i=0;i<4;i++)
             avg[n++]=val[i];                         
         printavg(avg);    
    }
    
    static void printavg(float avg[])
    {
            System.out.print(clas+" ");

            for(int i=0;i<12;i++)
            {  
                    System.out.print(i+1+":"+avg[i]+" ");
            }
            System.out.println("");
    }
    
    static float[] calavg(int histogram[])
    {
        float average[]=new float[10];
        for(int i=0;i<4;i++)
        {
            int sum=0;
            for(int j=i*64;j<i*64+64;j++)
            {
                sum+=histogram[j];
            }
            average[i]=((float)sum)/64;
        }
        
        return average;        
    }

}