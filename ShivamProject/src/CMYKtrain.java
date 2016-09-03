import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
 
 
public class CMYKtrain
{
        static int clas;
        static FileOutputStream fos;
        static File file;
        static PrintStream ps;
        public static void ImageTrain(String[] imgName,int cls) throws IOException        
{
           
            file = new File("src\\Data\\traincmyk.txt");
            PrintStream console = System.out;
            fos = new FileOutputStream(file,true);
             ps = new PrintStream(fos);
            //System.setOut(ps);
            clas=cls;                  		
            for(int i=0;i<imgName.length;i++)
            {
                    File f = new File(imgName[i]);
                      if(f.isFile())
                      {
                          ReadImage(f.getAbsolutePath().toString());
                      }
                      else
                      {
                          File[] f1=f.listFiles();
                          for(i=0;i<f1.length;i++)
                          {
                               ReadImage(f1[i].getAbsolutePath().toString());

                          }        
                      }
            }
            System.setOut(console);

}
	public static void ReadImage(String imgName) throws IOException
        {
            BufferedImage bi = null;
            try 
            {
                bi = ImageIO.read(new File(imgName));
            } 
            catch (IOException e) {}
            int histogramc[]=new int[256];
            int histogramm[]=new int[256];
            int histogramy[]=new int[256];
            int histogramk[]=new int[256];
		
             for (int bin = 0; bin < 255; bin++)
            {
                histogramc[bin] = 0;
                histogramm[bin]=0;
                histogramy[bin]=0;
                histogramk[bin]=0;
            }
               
            int x=bi.getHeight();
            int y=bi.getWidth();
            int m=0;
              
            int val[]=new int[30];
            for(int k=0;k<y;k++)
            {
                for(int l=0;l<x;l++)
                {
                     int rgb = bi.getRGB(k, l);
                     int r = (rgb >> 16) & 0xFF;
                     int g = (rgb >>8 ) & 0xFF;
                     int b = (rgb) & 0xFF;  
                    // System.out.println(r+"     "+g+"     "+b);
                      
                     val=rgbToCmyk(r,g,b);
                     
                    
                        histogramc[val[0]]++;
                        histogramm[val[1]]++;
                        histogramy[val[2]]++;
                        histogramk[val[3]]++; 
                 }
            }
            for(int i=0;i<255;i++) 
                    {
                       // System.out.println("value of "+i+"->"+histogramc[i]+" "+histogramm[i]+" "+histogramy[i]+" "+histogramk[i]);
                    }
             int sum=0;
        float avg[]=new float[20];
        int n=0;
            float value[]=new float[20];
            
            value=calavg(histogramc);
            for(int i=0;i<4;i++)
                avg[n++]=value[i];
            
            value=calavg(histogramm);
            for(int i=0;i<4;i++)
                avg[n++]=value[i];
            
            value=calavg(histogramy);
            for(int i=0;i<4;i++)
                avg[n++]=value[i];
            value=calavg(histogramk);
            for(int i=0;i<4;i++)
                avg[n++]=value[i];
       /*for(int i=0;i<16;i++)
           {
               for(int j=i*16;j<i*16+16;j++)
               {
                   sum+=histogramc[j]+histogramm[j]+histogramy[j]+histogramk[j];
               }
              // System.out.println(sum);
               //avg[i]=((float)sum)/16;
           }*/
        
        printavg(avg);
         
        
    
    }
static void printavg(float avg[])
{
  System.out.print(clas+" ");
  for(int i=0;i<12;i++)
  {
      System.out.print(i+1+":"+avg[i]+" ");
  }
  System.out.println();

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
            //System.out.print(" "+average[i]);
        }
        
        return average;
        
    }
        public static int[] rgbToCmyk(int red, int green, int blue)
        {
            int black = Math.min(Math.min(255 - red, 255 - green), 255 - blue);

            if (black!=255) 
            {
                int cyan    = (255-red-black);///(255-black);
                int magenta = (255-green-black);///(255-black);
                int yellow  = (255-blue-black);///(255-black);

                //System.out.println(cyan+"  "+magenta+"  "+yellow+"  "+black);
                return new int[] {cyan,magenta,yellow,black};
            } 
            else 
            {
                int cyan = 255 - red;
                int magenta = 255 - green;
                int yellow = 255 - blue;
                //System.out.println(cyan+"  "+magenta+"  "+yellow+"  "+black);
                return new int[] {cyan,magenta,yellow,black};
            }
        }
}
