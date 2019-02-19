import java.util.*;

class Node implements Comparable<Node>{
	int configuracao[][] = new int[4][4];
	int depth = 0;
	int cost_f = 0;
	Node pai;
	char path = ' ';

	Node(int n[][]){		
		configuracao = n;
		this.pai = pai;
	}


	public int compareTo(Node n){
		if(this.cost_f == n.cost_f )
			return 0;
		else if(this.cost_f > n.cost_f ) 
			return 1;
		return -1;
	}

	@Override
	public String toString(){
		String a = "";
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				a += this.configuracao[i][j];
			}
			a += " ";
		}
		a += " --"+ this.depth + "-- "+ this.cost_f + " --";
		return a;
	}
}
class Node2 implements Comparable<Node2>{
	int configuracao[][] = new int[4][4];
	int depth = 0;
	int cost_f = 0;
	Node2 pai;
	char path = ' ';

	Node2(int n[][]){		
		configuracao = n;
		this.pai = pai;
	}

	public int compareTo(Node2 n){
		if(this.cost_f > n.cost_f)
			return 1;
		else if(this.cost_f < n.cost_f)
			return-1;
		else{
			if(this.depth > n.depth)
				return 1;
			else if(this.depth < n.depth) 
				return -1;
			else 
				return 0;
		}
	}

	@Override
	public String toString(){
		String a = "";
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				a += this.configuracao[i][j];
			}
			a += " ";
		}
		a += " --"+ this.depth + "-- "+ this.cost_f + " --";
		return a;
	}
}

class Matriz{
	Node matriz_i,matriz_f;
	Node2 matriz_i2,matriz_f2;
	HashSet<String> tabela_hash = new HashSet<>();
	String key_final,key_inicial;
	double start;
	int nos = 0;

	Matriz(int inicial[][],int finale[][]){
		matriz_i = new Node(inicial);
		matriz_f = new Node(finale);
		matriz_i2 = new Node2(inicial);
		matriz_f2 = new Node2(finale);
		key_inicial = hash_key(inicial);
		tabela_hash.add(key_inicial);
		key_final = hash_key(finale);
	}

	public void DFS(){
		start = new Date().getTime();
		Stack<Node> pilha = new Stack<Node>();
		pilha.push(matriz_i);
		
		while(!pilha.empty()){
			Node node = pilha.pop();
			String key = hash_key(node.configuracao); // Gerar a chave para a hash_table
			if(key.equals(key_final)){                //verificar se encontrou a solucao
				encontrou(node);	
				System.exit(0);	
			}
			LinkedList<Node> descendentList = new LinkedList<>();		//lista para guardar os nos descendentes
			descendentList.addAll(MakeDescendents(node));
			nos += descendentList.size();	                 //guardar o numero de nos gerados
			while(!descendentList.isEmpty())	             // meter os nos gerados na pilha
				pilha.push(descendentList.pollLast());
		}
	}
	public void BFS(){
		start = new Date().getTime();
		LinkedList<Node> lista = new LinkedList<Node>();
		lista.add(matriz_i);

		while(!lista.isEmpty()){
			Node node = lista.removeFirst();
			String key = hash_key(node.configuracao); // Gerar a chave para a hash_table
			if(key.equals(key_final)){                //verificar se encontrou a solucao
				encontrou(node);
				System.exit(0);	
			}
			LinkedList<Node> descendentList = new LinkedList<>();		//lista para guardar os nos descendentes
			descendentList.addAll(MakeDescendents(node));
			nos += descendentList.size();                              //guardar o numero de nos gerados
			lista.addAll(descendentList);                              // meter os nos gerados na pilha
		}
	}
	public void DFS_ITERATIVO(){
		start = new Date().getTime();
		Stack<Node> pilha = new Stack<Node>();
		int MAX = 40;
		int profundidade = 0;
		while(profundidade <= MAX){
			pilha.push(matriz_i);
			tabela_hash.add(hash_key(matriz_i.configuracao));
			while(!pilha.isEmpty()){
				Node node = pilha.pop();
				String key = hash_key(node.configuracao);
				if(key.equals(key_final)){
					nos = tabela_hash.size() - 1;
					encontrou(node);
					System.exit(0);
				}	
				if(node.depth <= profundidade){
					LinkedList<Node> descendentList = new LinkedList<>();		
					descendentList.addAll(MakeDescendents(node));
					while(!descendentList.isEmpty())
						pilha.push(descendentList.pollLast());
				}
			}
			profundidade++;
			tabela_hash.clear();				//limpar a tabela de hash para poupar memoria , pois os nos vao ser criados de novo
			pilha.clear();
		}
	}
	public void GULOSA(){
		start = new Date().getTime();
		PriorityQueue<Node> lista = new PriorityQueue<Node>();	//PriorityQueue que ordena por ordem crescente do cost_f
		lista.add(matriz_i);
		while(!lista.isEmpty()){
			Node node = lista.remove();
			String key = hash_key(node.configuracao);
			if(key.equals(key_final)){
				encontrou(node);	
				System.exit(0);		
			}
			PriorityQueue<Node> descendentList = new PriorityQueue<Node>();		
			descendentList.addAll(MakeDescendents2(node));
			nos += descendentList.size();
			while(!descendentList.isEmpty())
				lista.add(descendentList.poll());	
		}
	}
	public void A_ESTRELA(){
		start = new Date().getTime();
		PriorityQueue<Node2> lista = new PriorityQueue<Node2>();  //PriorityQueue que ordena por ordem crescente do cost_f e em caso de empate 
		lista.add(matriz_i2);									  //ordena pela profundidade do nó: f = h + g	
		while(!lista.isEmpty()){
			Node2 node = lista.remove();
			String key = hash_key(node.configuracao);
			if(key.equals(key_final)){
				encontrou2(node);	
				System.exit(0);			
			}
			PriorityQueue<Node2> descendentList = new PriorityQueue<Node2>();		
			descendentList.addAll(MakeDescendents3(node));
			nos += descendentList.size();
			while(!descendentList.isEmpty())
				lista.add(descendentList.poll());
		}
	}
	public LinkedList<Node> MakeDescendents(Node no){	
		LinkedList<Node> list = new LinkedList<Node>();		
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(no.configuracao[i][j] == 0){
					//CIMA
					if(i >= 1){
						int[][] m = mover(no,'U',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.pai = no;
							no_filho.path = 'U';
							list.addLast(no_filho);
							tabela_hash.add(chave);
						}
					}
					//BAIXO
					if(i <= 2){
						int[][] m = mover(no,'D',i,j);
						String chave = hash_key(m);
				
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.pai = no;
							no_filho.path = 'D';
							list.addLast(no_filho);
							tabela_hash.add(chave);
						}
					}
					//DIREITA
					if(j <= 2){
						int[][] m = mover(no,'R',i,j);
						String chave = hash_key(m);
						
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.pai = no;
							no_filho.path = 'R';
							list.addLast(no_filho);
							tabela_hash.add(chave);
						}
					}
					//ESQUERDA
					if(j >= 1){
						int[][] m = mover(no,'L',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.pai = no;
							no_filho.path = 'L';
							list.addLast(no_filho);
							tabela_hash.add(chave);
						}
					}
				}		
			}
		}
		//System.out.println(list.toString());
		return list;
	}
	public PriorityQueue<Node> MakeDescendents2(Node no){	
		PriorityQueue<Node> list = new PriorityQueue<Node>();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(no.configuracao[i][j] == 0){
					//CIMA
					if(i >= 1){
						int[][] m = mover(no,'U',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;	
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'U';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//BAIXO
					if(i <= 2){
						int[][] m = mover(no,'D',i,j);
						String chave = hash_key(m);
				
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'D';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//DIREITA
					if(j <= 2){
						int[][] m = mover(no,'R',i,j);
						String chave = hash_key(m);
						
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'R';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//ESQUERDA
					if(j >= 1){
						int[][] m = mover(no,'L',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node no_filho = new Node(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'L';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
				}		
			}
		}
		return list;
	}
	public PriorityQueue<Node2> MakeDescendents3(Node2 no){	
		PriorityQueue<Node2> list = new PriorityQueue<Node2>();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(no.configuracao[i][j] == 0){
					//CIMA
					if(i >= 1){
						int[][] m = mover2(no,'U',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node2 no_filho = new Node2(m);
							no_filho.depth = no.depth + 1;	
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'U';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//BAIXO
					if(i <= 2){
						int[][] m = mover2(no,'D',i,j);
						String chave = hash_key(m);
				
						if(!tabela_hash.contains(chave)){
							Node2 no_filho = new Node2(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'D';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//DIREITA
					if(j <= 2){
						int[][] m = mover2(no,'R',i,j);
						String chave = hash_key(m);
						
						if(!tabela_hash.contains(chave)){
							Node2 no_filho = new Node2(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao) ;
							no_filho.pai = no;
							no_filho.path = 'R';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
					//ESQUERDA
					if(j >= 1){
						int[][] m = mover2(no,'L',i,j);
						String chave = hash_key(m);
					
						if(!tabela_hash.contains(chave)){
							Node2 no_filho = new Node2(m);
							no_filho.depth = no.depth + 1;
							no_filho.cost_f = heuristica(no_filho.configuracao,matriz_f.configuracao);
							no_filho.pai = no;
							no_filho.path = 'L';
							list.add(no_filho);
							tabela_hash.add(chave);
						}
					}
				}		
			}
		}
		return list;
	}
	public int[][] mover(Node no,char direcao,int i,int j){
		int clone[][] = new int[4][4];
		for(int x=0;x<4;x++)
			System.arraycopy(no.configuracao[x],0,clone[x],0,4);
		switch(direcao){
			case'U': 	clone[i][j] = clone[i-1][j];;
						clone[i-1][j] = 0;
						break;
			case'D': 	clone[i][j] = clone[i+1][j];
						clone[i+1][j] = 0;
						break;
			case'R':	clone[i][j] = clone[i][j+1];
						clone[i][j+1] = 0;
						break;
			case'L': 	clone[i][j] = clone[i][j-1];
						clone[i][j-1] = 0;
						break;
		}
		return clone;
	}
	public int[][] mover2(Node2 no,char direcao,int i,int j){
		int clone[][] = new int[4][4];
		for(int x=0;x<4;x++)
			System.arraycopy(no.configuracao[x],0,clone[x],0,4);
		switch(direcao){
			case'U': 	clone[i][j] = clone[i-1][j];;
						clone[i-1][j] = 0;
						break;
			case'D': 	clone[i][j] = clone[i+1][j];
						clone[i+1][j] = 0;
						break;
			case'R':	clone[i][j] = clone[i][j+1];
						clone[i][j+1] = 0;
						break;
			case'L': 	clone[i][j] = clone[i][j-1];
						clone[i][j-1] = 0;
						break;
		}
		return clone;
	}
	public void encontrou(Node no){
		Long end = new Date().getTime();
		System.out.println("Jogadas: " + no.depth);
		System.out.println("Numero de nos gerados: " + nos);
		System.out.print("Caminho: ");
		caminho(no);	
		System.out.println("Tempo de execução: " + ((end-start)/1000)+"s");
		double memoria = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.printf("Memoria utilizada: %.3fMB\n",memoria/(1024*1024));
	}
	public void encontrou2(Node2 no){
		Long end = new Date().getTime();
		System.out.println("Jogadas: " + no.depth);
		System.out.println("Numero de nos gerados: " + nos);
		System.out.print("Caminho: ");
		caminho2(no);	
		System.out.println("Tempo de execução: " + ((end-start)/1000)+"s");
		double memoria = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.printf("Memoria utilizada: %.3fMB\n",memoria/(1024*1024));
	}
	public int heuristica(int inicial[][],int finale[][]){
		int heuristica_total = 0;
		int i_x=0,i_y=0,f_x=0,f_y=0;
		for(int i=1;i<16;i++){

			for(int x=0;x<4;x++){
				for(int y=0;y<4;y++){
					if(inicial[x][y] == i){
						i_x = x;
						i_y = y;
					}
				}
			}

			for(int x=0;x<4;x++){
				for(int y=0;y<4;y++){
					if(finale[x][y] == i){
						f_x = x;
						f_y = y;
					}
				}
			}
		int manhattan_distance = (Math.abs(i_x-f_x) + Math.abs(i_y-f_y));
		heuristica_total += manhattan_distance;
		}
		return heuristica_total; //Manhattan distance - distancia em linha reta de uma configuracao até á configuracao final
	}
	public void caminho(Node no){
		System.out.println();
		char caminho[] = new char[99999999];
		int z = 0;
		while (no != null){
			caminho[z] = no.path;
			no = no.pai;
			z++;
		}
		for(int i=z;i>=0;i--)
			System.out.print(caminho[i] + " ");
		System.out.println();
	}
	public void caminho2(Node2 no){
		System.out.println();
		char caminho[] = new char[99999999];
		int z = 0;
		while (no != null){
			caminho[z] = no.path;
			no = no.pai;
			z++;
		}
		for(int i=z;i>=0;i--)
			System.out.print(caminho[i] + " ");
		System.out.println();
	}
	public String hash_key(int matriz[][]){
		String key = "";
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				char a = (char)matriz[i][j];
				a += 65;	
				key = key + a;		
			}
		}
		return key;
	}	

}

public class JOGO{
	static Scanner input;	
	static int matriz_inicial[][] = new int[4][4];
	static int matriz_final[][] = new int[4][4];
 	static Matriz jogo;

 	public static void main(String args[]){
		input = new Scanner(System.in);
		boolean solucao;

		Input();
		
		if(verificar_solucao()){     
			System.out.println();
			System.out.println("-----O JOGO TEM SOLUCAO-----");
			System.out.println();
			jogo = new Matriz(matriz_inicial,matriz_final);	
			escolher_busca();
		}
		else{
			System.out.println();
			System.out.println("-----O JOGO NAO TEM SOLUCAO!-----");
			System.out.println();
		}
	}

	static void Input(){
		System.out.println("-----JOGO DOS 15-----");
		System.out.println();
		System.out.println("Tabela inicial: ");

		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				matriz_inicial[i][j] = input.nextInt();
			}
		}

		System.out.println();
		System.out.println("Tabela Final: ");

		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				matriz_final[i][j] = input.nextInt();
			}
		}
	}

	static boolean verificar_solucao(){
		int array_First[] = new int[16];
		int array_Last[] = new int[16];
		int inversoes_First = 0;
		int inversoes_Last = 0;
		boolean first = false;
		boolean last = false;
		int p_zero_inicial = 0,p_zero_final = 0;

		int a = 0;
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(matriz_inicial[i][j] == 0)
					p_zero_inicial = i;
				array_First[a] = matriz_inicial[i][j];  //Converter a matriz num array, linha onde se encontra o zero
				a++;
			}
		}
	
		for(int i=0;i<16;i++){
			for(int j=i+1;j<16;j++){
				if(array_First[i] > array_First[j] && array_First[j] != 0)
					inversoes_First++;					//Contar o numero de inversoes
			}
		}
	
		a = 0;
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(matriz_final[i][j] == 0)
					p_zero_final = i;
				array_Last[a] = matriz_final[i][j];
				a++;
			}
		}

		for(int i=0;i<16;i++){
			for(int j=i+1;j<16;j++){
				if(array_Last[i] > array_Last[j] && array_Last[j] != 0)
					inversoes_Last++;
			}
		}
		//true se forem invertidos; zero-Par , inversoes-Impar e vice-versa
		if((p_zero_inicial%2 == 0 && inversoes_First%2 != 0) || (p_zero_inicial%2 != 0 && inversoes_First%2 == 0))	
			first = true;
		if((p_zero_final%2 == 0 && inversoes_Last%2 != 0) || (p_zero_final%2 != 0 && inversoes_Last%2 == 0))	
			last = true; 
		return first == last;  //Existe Solucao se ambos forem true ou ambos forem false.
	}

	static void escolher_busca(){
		Scanner input = new Scanner(System.in);
		System.out.println("Escolha um tipo de busca: ");
		System.out.println("    1- DFS   ");
		System.out.println("    2- BFS   ");
		System.out.println("    3- DFS ITERATIVO   ");
		System.out.println("    4- GULOSA   ");
		System.out.println("    5- A*   ");
		int escolha = input.nextInt();
		switch(escolha){	
			case 1: System.out.println();System.out.println("A FAZER A BUSCA . . .");System.out.println();jogo.DFS();
			case 2: System.out.println();System.out.println("A FAZER A BUSCA . . .");System.out.println();jogo.BFS();
			case 3: System.out.println();System.out.println("A FAZER A BUSCA . . .");System.out.println();jogo.DFS_ITERATIVO();
			case 4:	System.out.println();System.out.println("A FAZER A BUSCA . . .");System.out.println();jogo.GULOSA();
			case 5:	System.out.println();System.out.println("A FAZER A BUSCA . . .");System.out.println();jogo.A_ESTRELA();
			default: throw new Error("O NUMERO QUE ESCOLHEU NAO PERTENCE AO MENU.");
		}
	}

	
}


