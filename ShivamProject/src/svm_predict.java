import java.io.*;
import java.util.*;
import libsvm.*;

class svm_predict {
        static FileOutputStream fos;
        static File file;
        static int len=0;
        static PrintStream ps;
        static String [] name;//=new String[Test.size+1];
        static String [] line1;//=new String[Test.size+1];
	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) 
	{
		svm_print_string.print(s);
	}

	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		while(true)
		{
			String line = input.readLine();
			if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
                                //System.out.println("v="+v);
				for(int j=0;j<nr_class;j++)
                                {
					output.writeBytes(prob_estimates[j]+" ");
                                        System.out.println("prob_estimates["+j+"]="+prob_estimates[j]);
                                }
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
                                //System.out.println("v="+v);
			}

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		/*if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
			svm_predict.info("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");*/
	}

	private static void exit_with_help()
	{
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static void Begin() throws IOException
	{
		int i, predict_probability=0;
        	svm_print_string = svm_print_stdout;

		// parse options
	/*	for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();*/
		try 
		{
			BufferedReader input = new BufferedReader(new FileReader("src\\Data\\test.txt"));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("src\\Data\\output.txt")));
			int op=Start.choice;
                        svm_model model=new svm_model();
                        if(op==1)
                            model = svm.svm_load_model("src\\Data\\model.txt");
                        else if(op==2)
                            model = svm.svm_load_model("src\\Data\\modelCMYK.txt");
                            
			if (model == null)
			{
				//System.err.print("can't open model file "+argv[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			predict(input,output,model,predict_probability);
			input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
	}
        public void makefile() throws IOException
        {
            name=new String[Test.size+1];
            line1=new String[Test.size+1];
            BufferedReader input1 = new BufferedReader(new FileReader("src\\Data\\test1.txt"));
            BufferedReader input2 = new BufferedReader(new FileReader("src\\Data\\output.txt"));
            file = new File("src\\Data\\outfile.txt");
            fos = new FileOutputStream(file);
            ps = new PrintStream(fos);
            int v;
            int i=0;
            System.setOut(ps);
            System.out.println("Test.size="+Test.size);
            
            line1[i]=input1.readLine();
            String line2=input2.readLine();
            while(line1[i]!=null && line2!=null)
            {
                v=(int)atof(line2);
                
                
                switch(v)
                {
                    case 1:name[i]="BOAT";
                            break;
                    case 2:name[i]="BUTTERFLY";
                            break;
                    case 3:name[i]="CARS";
                            break;
                    case 4:name[i]="FLOWER";
                            break;
                    case 5:name[i]="FOOD";
                            break;
                    case 6:name[i]="HUMAN";
                            break;
                    case 7:name[i]="MOUNTAINS";
                            break;
                    case 8:name[i]="RIVERS";
                            break;
                    case 9:name[i]="SPORTS";
                            break;
                    case 10:name[i]="SUNSET";
                            break;
                    default:name[i]="UNKNOWN";
                            break;
                }
                
                System.out.println(line1[i]+":"+name[i]);
                i++;
                len=i;
                line1[i]=input1.readLine();
                line2=input2.readLine();
                
            }
			//if(line == null) break;
            
            
			
        }
}
