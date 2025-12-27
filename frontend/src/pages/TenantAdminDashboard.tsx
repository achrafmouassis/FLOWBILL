import { useEffect, useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import ChangePasswordModal from '../components/ChangePasswordModal';

interface CompetencyGroup {
  id: number;
  name: string;
  description?: string;
  category: string;
}

interface User {
  id: number;
  email: string;
  fullName: string;
  telephone: string;
  enabled: boolean;
  tenantId: string;
  role: string;
  competencyGroups: CompetencyGroup[];
}

// Fonction pour générer des mots de passe sécurisés
const generateStrongPassword = (length = 12): string => {
  const lowercase = 'abcdefghijklmnopqrstuvwxyz';
  const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const numbers = '0123456789';
  const symbols = '!@#$%^&*()_+-=[]{}|;:,.<>?';
  
  const allChars = lowercase + uppercase + numbers + symbols;
  
  // S'assurer d'au moins un caractère de chaque type
  const password = [
    lowercase[Math.floor(Math.random() * lowercase.length)],
    uppercase[Math.floor(Math.random() * uppercase.length)],
    numbers[Math.floor(Math.random() * numbers.length)],
    symbols[Math.floor(Math.random() * symbols.length)]
  ];
  
  // Remplir le reste de la longueur
  for (let i = password.length; i < length; i++) {
    password.push(allChars[Math.floor(Math.random() * allChars.length)]);
  }
  
  // Mélanger
  for (let i = password.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [password[i], password[j]] = [password[j], password[i]];
  }
  
  return password.join('');
};

const TenantAdminDashboard = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [newUserEmail, setNewUserEmail] = useState('');
  const [newUserFullName, setNewUserFullName] = useState('');
  const [newUserTelephone, setNewUserTelephone] = useState('');
  const [selectedCompetencies, setSelectedCompetencies] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [generatedPassword, setGeneratedPassword] = useState('');
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [currentUserEmail, setCurrentUserEmail] = useState('');
  const [competencyGroups, setCompetencyGroups] = useState<CompetencyGroup[]>([]);
  const [showCompetencyModal, setShowCompetencyModal] = useState(false);
  const [selectedUserForCompetencies, setSelectedUserForCompetencies] = useState<User | null>(null);
  const [loadingCompetencies, setLoadingCompetencies] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const token = localStorage.getItem('token');
  const decoded: any = token ? jwtDecode(token) : {};
  const tenantId = decoded.tenantId || decoded.tenant;

  const navigate = useNavigate();

  useEffect(() => {
    if (tenantId) {
      fetchUsers();
      fetchCompetencyGroups();
      setCurrentUserEmail(decoded.sub);
    }
  }, [tenantId]);

  const fetchUsers = async () => {
    try {
      const response = await api.get(`/auth/users/${tenantId}`);
      // Filtrer seulement les développeurs
      const developers = response.data.filter((user: any) => user.role === 'DEVELOPER');
      const usersWithCompetencies = developers.map((user: any) => ({
        ...user,
        competencyGroups: user.competencyGroups || []
      }));
      setUsers(usersWithCompetencies);
    } catch (error) {
      console.error('Failed to fetch users', error);
      alert('Erreur lors de la récupération des développeurs');
    } finally {
      setLoading(false);
    }
  };

  const fetchCompetencyGroups = async () => {
    setLoadingCompetencies(true);
    try {
      // Compétences UNIQUEMENT pour développeurs
      const developerCompetencies: CompetencyGroup[] = [
        // Langages de programmation
        { id: 1, name: 'Java', description: 'Java 8+, Spring Boot, Hibernate', category: 'Langages' },
        { id: 2, name: 'Python', description: 'Python 3, Django, Flask, FastAPI', category: 'Langages' },
        { id: 3, name: 'JavaScript', description: 'ES6+, Node.js, npm/yarn', category: 'Langages' },
        { id: 4, name: 'TypeScript', description: 'TypeScript, typage statique', category: 'Langages' },
        { id: 5, name: 'C#', description: '.NET Core, ASP.NET, Entity Framework', category: 'Langages' },
        { id: 6, name: 'PHP', description: 'PHP 7+, Laravel, Symfony', category: 'Langages' },
        { id: 7, name: 'Ruby', description: 'Ruby on Rails', category: 'Langages' },
        { id: 8, name: 'Go', description: 'Golang, Goroutines, Gin', category: 'Langages' },
        { id: 9, name: 'Kotlin', description: 'Android, Spring Boot', category: 'Langages' },
        { id: 10, name: 'Swift', description: 'iOS, macOS development', category: 'Langages' },
        { id: 11, name: 'Rust', description: 'Système, performance', category: 'Langages' },
        { id: 12, name: 'Scala', description: 'Scala, Akka, Play Framework', category: 'Langages' },
        
        // Frameworks Frontend
        { id: 13, name: 'React', description: 'React 18, Hooks, Context API', category: 'Frontend' },
        { id: 14, name: 'Angular', description: 'Angular 15+, RxJS, NgRx', category: 'Frontend' },
        { id: 15, name: 'Vue.js', description: 'Vue 3, Vuex, Vue Router', category: 'Frontend' },
        { id: 16, name: 'Next.js', description: 'React framework, SSR', category: 'Frontend' },
        { id: 17, name: 'Nuxt.js', description: 'Vue framework, SSR', category: 'Frontend' },
        { id: 18, name: 'Svelte', description: 'Svelte/SvelteKit', category: 'Frontend' },
        
        // Frameworks Backend
        { id: 19, name: 'Spring Boot', description: 'Spring MVC, Security, Data', category: 'Backend' },
        { id: 20, name: 'Express.js', description: 'Node.js framework', category: 'Backend' },
        { id: 21, name: 'Django', description: 'Python web framework', category: 'Backend' },
        { id: 22, name: 'Flask', description: 'Micro framework Python', category: 'Backend' },
        { id: 23, name: 'Laravel', description: 'PHP framework', category: 'Backend' },
        { id: 24, name: 'FastAPI', description: 'API moderne Python', category: 'Backend' },
        { id: 25, name: 'NestJS', description: 'Node.js framework TypeScript', category: 'Backend' },
        
        // Bases de données
        { id: 26, name: 'MySQL', description: 'Base relationnelle', category: 'Bases de données' },
        { id: 27, name: 'PostgreSQL', description: 'Base relationnelle avancée', category: 'Bases de données' },
        { id: 28, name: 'MongoDB', description: 'Base NoSQL document', category: 'Bases de données' },
        { id: 29, name: 'Redis', description: 'Cache, sessions, queue', category: 'Bases de données' },
        { id: 30, name: 'Oracle', description: 'Base de données d\'entreprise', category: 'Bases de données' },
        { id: 31, name: 'SQL Server', description: 'Microsoft SQL Server', category: 'Bases de données' },
        { id: 32, name: 'Cassandra', description: 'Base NoSQL scalable', category: 'Bases de données' },
        { id: 33, name: 'Elasticsearch', description: 'Recherche et analyse', category: 'Bases de données' },
        { id: 34, name: 'DynamoDB', description: 'NoSQL AWS', category: 'Bases de données' },
        
        // DevOps & Cloud
        { id: 35, name: 'Docker', description: 'Conteneurisation', category: 'DevOps & Cloud' },
        { id: 36, name: 'Kubernetes', description: 'Orchestration de conteneurs', category: 'DevOps & Cloud' },
        { id: 37, name: 'AWS', description: 'Amazon Web Services', category: 'DevOps & Cloud' },
        { id: 38, name: 'Azure', description: 'Microsoft Cloud', category: 'DevOps & Cloud' },
        { id: 39, name: 'GCP', description: 'Google Cloud Platform', category: 'DevOps & Cloud' },
        { id: 40, name: 'CI/CD', description: 'Jenkins, GitLab CI, GitHub Actions', category: 'DevOps & Cloud' },
        { id: 41, name: 'Terraform', description: 'Infrastructure as Code', category: 'DevOps & Cloud' },
        { id: 42, name: 'Ansible', description: 'Automation', category: 'DevOps & Cloud' },
        { id: 43, name: 'Prometheus', description: 'Monitoring', category: 'DevOps & Cloud' },
        { id: 44, name: 'Grafana', description: 'Visualisation de données', category: 'DevOps & Cloud' },
        
        // Mobile
        { id: 45, name: 'React Native', description: 'Mobile cross-platform', category: 'Mobile' },
        { id: 46, name: 'Flutter', description: 'Dart, cross-platform', category: 'Mobile' },
        { id: 47, name: 'Android Native', description: 'Kotlin/Java', category: 'Mobile' },
        { id: 48, name: 'iOS Native', description: 'Swift', category: 'Mobile' },
        { id: 49, name: 'Ionic', description: 'Hybrid mobile', category: 'Mobile' },
        
        // Outils & Méthodologies
        { id: 50, name: 'Git', description: 'Version control', category: 'Outils & Méthodologies' },
        { id: 51, name: 'Agile/Scrum', description: 'Méthodologies Agile', category: 'Outils & Méthodologies' },
        { id: 52, name: 'TDD/BDD', description: 'Test Driven Development', category: 'Outils & Méthodologies' },
        { id: 53, name: 'Microservices', description: 'Architecture microservices', category: 'Outils & Méthodologies' },
        { id: 54, name: 'REST API', description: 'API RESTful', category: 'Outils & Méthodologies' },
        { id: 55, name: 'GraphQL', description: 'Query language', category: 'Outils & Méthodologies' },
        { id: 56, name: 'WebSocket', description: 'Communication temps réel', category: 'Outils & Méthodologies' },
        { id: 57, name: 'gRPC', description: 'RPC framework', category: 'Outils & Méthodologies' },
        { id: 58, name: 'Apache Kafka', description: 'Streaming platform', category: 'Outils & Méthodologies' },
        
        // Sécurité
        { id: 59, name: 'OWASP', description: 'Sécurité web', category: 'Sécurité' },
        { id: 60, name: 'JWT', description: 'JSON Web Tokens', category: 'Sécurité' },
        { id: 61, name: 'OAuth2/OpenID', description: 'Authentification', category: 'Sécurité' },
        { id: 62, name: 'SSL/TLS', description: 'Sécurité réseau', category: 'Sécurité' },
        { id: 63, name: 'Penetration Testing', description: 'Tests de pénétration', category: 'Sécurité' },
        
        // Testing
        { id: 64, name: 'JUnit', description: 'Tests Java', category: 'Testing' },
        { id: 65, name: 'Jest', description: 'Testing JavaScript', category: 'Testing' },
        { id: 66, name: 'Cypress', description: 'E2E testing', category: 'Testing' },
        { id: 67, name: 'Selenium', description: 'Tests automatisés web', category: 'Testing' },
        { id: 68, name: 'Postman', description: 'API testing', category: 'Testing' },
        { id: 69, name: 'Mockito', description: 'Mocking framework', category: 'Testing' },
        
        // Spécialisations
        { id: 70, name: 'Machine Learning', description: 'TensorFlow, PyTorch, scikit-learn', category: 'Spécialisations' },
        { id: 71, name: 'Data Science', description: 'Pandas, NumPy, Matplotlib', category: 'Spécialisations' },
        { id: 72, name: 'Blockchain', description: 'Smart Contracts, Solidity', category: 'Spécialisations' },
        { id: 73, name: 'IoT', description: 'Internet des Objets', category: 'Spécialisations' },
        { id: 74, name: 'Big Data', description: 'Hadoop, Spark, Hive', category: 'Spécialisations' },
        { id: 75, name: 'DevOps', description: 'Culture DevOps', category: 'Spécialisations' },
        { id: 76, name: 'Site Reliability', description: 'SRE principles', category: 'Spécialisations' },
        { id: 77, name: 'Game Development', description: 'Unity, Unreal Engine', category: 'Spécialisations' },
        { id: 78, name: 'AR/VR', description: 'Réalité augmentée/virtuelle', category: 'Spécialisations' },
      ];
      
      setCompetencyGroups(developerCompetencies);
    } catch (error) {
      console.error('Failed to fetch competency groups', error);
      setCompetencyGroups([]);
    } finally {
      setLoadingCompetencies(false);
    }
  };

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setGeneratedPassword('');
    
    const password = generateStrongPassword();

    try {
      await api.post('/auth/register', {
        email: newUserEmail,
        password: password,
        fullName: newUserFullName,
        telephone: newUserTelephone,
        role: 'DEVELOPER', // TOUJOURS DEVELOPER
        tenantId: tenantId,
        competencyGroupIds: selectedCompetencies
      });
      
      setGeneratedPassword(password);
      setNewUserEmail('');
      setNewUserFullName('');
      setNewUserTelephone('');
      setSelectedCompetencies([]);
      fetchUsers();
    } catch (error) {
      console.error('Failed to create user', error);
      alert('Erreur lors de la création du développeur');
    }
  };

  const handleAssignCompetencies = async (userId: number) => {
    try {
      setUsers(prevUsers => 
        prevUsers.map(user => 
          user.id === userId 
            ? { 
                ...user, 
                competencyGroups: competencyGroups.filter(cg => 
                  selectedCompetencies.includes(cg.id)
                )
              } 
            : user
        )
      );
      
      alert('Compétences mises à jour');
      setShowCompetencyModal(false);
      setSelectedUserForCompetencies(null);
      setSelectedCompetencies([]);
    } catch (error) {
      console.error('Failed to assign competencies', error);
      alert('Erreur lors de l\'assignation des compétences');
    }
  };

  const openCompetencyModal = (user: User) => {
    setSelectedUserForCompetencies(user);
    const userCompetencies = user.competencyGroups || [];
    setSelectedCompetencies(userCompetencies.map(cg => cg.id));
    setShowCompetencyModal(true);
  };

  const toggleCompetencySelection = (competencyId: number) => {
    setSelectedCompetencies(prev => 
      prev.includes(competencyId)
        ? prev.filter(id => id !== competencyId)
        : [...prev, competencyId]
    );
  };

  const doLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const getCategoryColor = (category: string) => {
    const colors: Record<string, string> = {
      'Langages': 'bg-red-100 text-red-800',
      'Frontend': 'bg-blue-100 text-blue-800',
      'Backend': 'bg-green-100 text-green-800',
      'Bases de données': 'bg-purple-100 text-purple-800',
      'DevOps & Cloud': 'bg-yellow-100 text-yellow-800',
      'Mobile': 'bg-indigo-100 text-indigo-800',
      'Outils & Méthodologies': 'bg-pink-100 text-pink-800',
      'Sécurité': 'bg-gray-100 text-gray-800',
      'Testing': 'bg-teal-100 text-teal-800',
      'Spécialisations': 'bg-orange-100 text-orange-800',
    };
    return colors[category] || 'bg-gray-100 text-gray-800';
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text).then(() => {
      alert('Mot de passe copié dans le presse-papier!');
    });
  };

  // Grouper les compétences par catégorie
  const groupedCompetencies = competencyGroups.reduce((acc, competency) => {
    if (!acc[competency.category]) {
      acc[competency.category] = [];
    }
    acc[competency.category].push(competency);
    return acc;
  }, {} as Record<string, CompetencyGroup[]>);

  // Filtrer les compétences par recherche
  const filteredCompetencies = searchTerm 
    ? competencyGroups.filter(competency =>
        competency.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (competency.description && competency.description.toLowerCase().includes(searchTerm.toLowerCase())) ||
        competency.category.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : competencyGroups;

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-xl font-bold text-gray-800">Tableau de bord Développeurs - {tenantId}</h1>
          <div className="flex items-center space-x-4">
            <button onClick={() => setIsPasswordModalOpen(true)} className="text-blue-600 hover:text-blue-800">
              Changer mot de passe
            </button>
            <button onClick={doLogout} className="text-red-500 hover:text-red-700">
              Déconnexion
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Formulaire de création de développeur */}
        <div className="bg-white p-6 rounded-lg shadow mb-8">
          <h2 className="text-lg font-semibold mb-4">Ajouter un nouveau développeur</h2>
          <form onSubmit={handleCreateUser} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Nom complet *</label>
                <input
                  type="text"
                  className="mt-1 border p-2 rounded w-full"
                  value={newUserFullName}
                  onChange={(e) => setNewUserFullName(e.target.value)}
                  required
                  placeholder="John Doe"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Email *</label>
                <input
                  type="email"
                  className="mt-1 border p-2 rounded w-full"
                  value={newUserEmail}
                  onChange={(e) => setNewUserEmail(e.target.value)}
                  required
                  placeholder="john.doe@example.com"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Téléphone</label>
                <input
                  type="tel"
                  className="mt-1 border p-2 rounded w-full"
                  value={newUserTelephone}
                  onChange={(e) => setNewUserTelephone(e.target.value)}
                  placeholder="+33 1 23 45 67 89"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Rôle *</label>
                <div className="mt-1 p-2 rounded w-full bg-green-100 text-green-800 font-medium">
                  Développeur
                </div>
                <p className="text-xs text-gray-500 mt-1">Seul le rôle Développeur est disponible</p>
              </div>
            </div>

            {/* Sélection des compétences techniques */}
            <div>
              <div className="flex justify-between items-center mb-2">
                <label className="block text-sm font-medium text-gray-700">
                  Compétences techniques *
                </label>
                <span className="text-xs text-gray-500">
                  {selectedCompetencies.length} sélectionnée(s)
                </span>
              </div>
              
              {/* Barre de recherche */}
              <div className="mb-3">
                <input
                  type="text"
                  placeholder="Rechercher une compétence (ex: Java, React, Docker...)"
                  className="w-full border p-2 rounded text-sm"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
              
              {loadingCompetencies ? (
                <div className="text-gray-500 text-sm">Chargement des compétences...</div>
              ) : competencyGroups.length > 0 ? (
                <div className="border rounded p-4 max-h-96 overflow-y-auto">
                  {searchTerm ? (
                    // Affichage avec recherche
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      {filteredCompetencies.map((competency) => (
                        <div key={competency.id} className="flex items-center p-2 hover:bg-gray-50 rounded">
                          <input
                            type="checkbox"
                            id={`comp-${competency.id}`}
                            checked={selectedCompetencies.includes(competency.id)}
                            onChange={() => toggleCompetencySelection(competency.id)}
                            className="h-4 w-4 text-blue-600 rounded"
                          />
                          <label htmlFor={`comp-${competency.id}`} className="ml-3 flex-1">
                            <div className="flex items-center">
                              <span className="text-sm font-medium text-gray-700">{competency.name}</span>
                              <span className={`ml-2 px-2 py-0.5 text-xs rounded-full ${getCategoryColor(competency.category)}`}>
                                {competency.category}
                              </span>
                            </div>
                            {competency.description && (
                              <p className="text-xs text-gray-500 mt-1">{competency.description}</p>
                            )}
                          </label>
                        </div>
                      ))}
                    </div>
                  ) : (
                    // Affichage groupé par catégorie
                    <div className="space-y-6">
                      {Object.entries(groupedCompetencies).map(([category, comps]) => (
                        <div key={category}>
                          <div className="flex items-center mb-2">
                            <h4 className="text-sm font-semibold text-gray-700">{category}</h4>
                            <span className="ml-2 text-xs text-gray-500">({comps.length})</span>
                          </div>
                          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2 ml-2">
                            {comps.map((competency) => (
                              <div key={competency.id} className="flex items-center p-1 hover:bg-gray-50 rounded">
                                <input
                                  type="checkbox"
                                  id={`comp-${competency.id}`}
                                  checked={selectedCompetencies.includes(competency.id)}
                                  onChange={() => toggleCompetencySelection(competency.id)}
                                  className="h-4 w-4 text-blue-600 rounded"
                                />
                                <label htmlFor={`comp-${competency.id}`} className="ml-2 text-sm text-gray-700">
                                  {competency.name}
                                </label>
                              </div>
                            ))}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ) : (
                <div className="text-gray-500 text-sm italic">
                  Aucune compétence disponible
                </div>
              )}
              <p className="text-xs text-gray-500 mt-2">
                Sélectionnez au moins une compétence technique
              </p>
            </div>

            <button
              type="submit"
              disabled={selectedCompetencies.length === 0}
              className={`p-2 rounded w-full md:w-auto ${
                selectedCompetencies.length === 0
                  ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  : 'bg-green-600 text-white hover:bg-green-700'
              }`}
            >
              {selectedCompetencies.length === 0 
                ? 'Sélectionnez au moins une compétence' 
                : 'Ajouter le développeur'}
            </button>
          </form>

          {generatedPassword && (
            <div className="mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded">
              <p className="font-bold text-yellow-800">✅ Développeur créé avec succès!</p>
              <div className="mt-2 space-y-2">
                <div>
                  <p className="text-sm font-medium text-gray-700">Nom:</p>
                  <p className="font-semibold">{newUserFullName}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700">Email:</p>
                  <p className="font-mono bg-gray-100 p-2 rounded">{newUserEmail}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700">Rôle:</p>
                  <span className="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                    Développeur
                  </span>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700">Mot de passe temporaire:</p>
                  <div className="flex items-center">
                    <p className="font-mono font-bold text-lg bg-gray-100 p-2 rounded flex-1">
                      {generatedPassword}
                    </p>
                    <button
                      onClick={() => copyToClipboard(generatedPassword)}
                      className="ml-2 text-blue-600 hover:text-blue-800 p-2"
                      title="Copier dans le presse-papier"
                    >
                      📋 Copier
                    </button>
                  </div>
                </div>
                {selectedCompetencies.length > 0 && (
                  <div>
                    <p className="text-sm font-medium text-gray-700">Compétences sélectionnées:</p>
                    <div className="flex flex-wrap gap-1 mt-1">
                      {selectedCompetencies.slice(0, 5).map(compId => {
                        const comp = competencyGroups.find(c => c.id === compId);
                        return comp ? (
                          <span
                            key={comp.id}
                            className={`px-2 py-1 text-xs rounded-full ${getCategoryColor(comp.category)}`}
                          >
                            {comp.name}
                          </span>
                        ) : null;
                      })}
                      {selectedCompetencies.length > 5 && (
                        <span className="text-xs text-gray-500">
                          +{selectedCompetencies.length - 5} autres
                        </span>
                      )}
                    </div>
                  </div>
                )}
              </div>
              <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded">
                <p className="text-sm font-bold text-red-700 mb-1">⚠️ Instructions importantes</p>
                <ul className="text-xs text-red-600 space-y-1">
                  <li>• Ce mot de passe est temporaire et sécurisé (12 caractères complexes)</li>
                  <li>• Copiez-le maintenant - il ne sera plus affiché après actualisation</li>
                  <li>• Le développeur devra changer son mot de passe à la première connexion</li>
                  <li>• Envoyez ces informations par un canal sécurisé</li>
                </ul>
              </div>
            </div>
          )}
        </div>

        {/* Tableau des développeurs */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="p-6 border-b flex justify-between items-center">
            <h2 className="text-lg font-semibold">Équipe de Développeurs</h2>
            <span className="text-sm text-gray-500">
              {users.length} développeur{users.length !== 1 ? 's' : ''}
            </span>
          </div>
          
          {loading ? (
            <div className="p-6 text-center">Chargement des développeurs...</div>
          ) : users.length === 0 ? (
            <div className="p-6 text-center text-gray-500">
              <p className="mb-2">Aucun développeur dans l'équipe</p>
              <p className="text-sm">Commencez par ajouter votre premier développeur</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Développeur
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Contact
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Compétences
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Statut
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {users.map((user) => {
                    const userCompetencies = user.competencyGroups || [];
                    
                    return (
                      <tr key={user.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          #{user.id.toString().padStart(3, '0')}
                        </td>
                        <td className="px-6 py-4">
                          <div>
                            <div className="font-medium text-gray-900">{user.fullName}</div>
                            <div className="text-sm text-gray-500">{user.email}</div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          {user.telephone ? (
                            <a 
                              href={`tel:${user.telephone}`} 
                              className="text-blue-600 hover:text-blue-800 text-sm"
                            >
                              {user.telephone}
                            </a>
                          ) : (
                            <span className="text-gray-400 text-sm">Non renseigné</span>
                          )}
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex flex-wrap gap-1">
                            {userCompetencies.length > 0 ? (
                              <>
                                {userCompetencies.slice(0, 3).map((competency) => (
                                  <span
                                    key={competency.id}
                                    className={`px-2 py-1 text-xs rounded-full ${getCategoryColor(competency.category)}`}
                                    title={`${competency.name} - ${competency.category}`}
                                  >
                                    {competency.name}
                                  </span>
                                ))}
                                {userCompetencies.length > 3 && (
                                  <span className="text-xs text-gray-500" title={`${userCompetencies.length - 3} compétences supplémentaires`}>
                                    +{userCompetencies.length - 3}
                                  </span>
                                )}
                              </>
                            ) : (
                              <span className="text-gray-400 text-sm">Aucune compétence</span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            user.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                          }`}>
                            {user.enabled ? '🟢 Actif' : '🔴 Inactif'}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <button
                            onClick={() => openCompetencyModal(user)}
                            className="text-blue-600 hover:text-blue-900 text-sm font-medium px-3 py-1 border border-blue-300 rounded hover:bg-blue-50"
                          >
                            Gérer compétences
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Modal d'assignation des compétences */}
      {showCompetencyModal && selectedUserForCompetencies && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-4xl w-full mx-4 my-8 max-h-[90vh] overflow-hidden flex flex-col">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">
                Gérer les compétences pour {selectedUserForCompetencies.fullName}
              </h3>
              <button
                onClick={() => {
                  setShowCompetencyModal(false);
                  setSelectedUserForCompetencies(null);
                  setSelectedCompetencies([]);
                }}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            
            {/* Barre de recherche dans le modal */}
            <div className="mb-4">
              <input
                type="text"
                placeholder="Rechercher une compétence..."
                className="w-full border p-2 rounded"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            
            <div className="space-y-4 mb-6 flex-1 overflow-y-auto pr-2">
              {Object.entries(groupedCompetencies).map(([category, comps]) => {
                const filteredComps = comps.filter(competency =>
                  competency.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                  (competency.description && competency.description.toLowerCase().includes(searchTerm.toLowerCase()))
                );
                
                if (filteredComps.length === 0) return null;
                
                return (
                  <div key={category}>
                    <h4 className="text-sm font-semibold text-gray-700 mb-2 sticky top-0 bg-white py-1">
                      {category} ({filteredComps.length})
                    </h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2 ml-2">
                      {filteredComps.map((competency) => (
                        <div key={competency.id} className="flex items-center p-2 hover:bg-gray-50 rounded">
                          <input
                            type="checkbox"
                            id={`modal-comp-${competency.id}`}
                            checked={selectedCompetencies.includes(competency.id)}
                            onChange={() => toggleCompetencySelection(competency.id)}
                            className="h-4 w-4 text-blue-600 rounded"
                          />
                          <label htmlFor={`modal-comp-${competency.id}`} className="ml-3 flex-1">
                            <span className="text-sm font-medium text-gray-700">{competency.name}</span>
                            {competency.description && (
                              <p className="text-xs text-gray-500">{competency.description}</p>
                            )}
                          </label>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
            <div className="pt-4 border-t mt-4">
              <div className="flex justify-between items-center mb-4">
                <div className="text-sm text-gray-600">
                  {selectedCompetencies.length} compétence{selectedCompetencies.length !== 1 ? 's' : ''} sélectionnée{selectedCompetencies.length !== 1 ? 's' : ''}
                </div>
                <div className="flex space-x-3">
                  <button
                    onClick={() => {
                      setShowCompetencyModal(false);
                      setSelectedUserForCompetencies(null);
                      setSelectedCompetencies([]);
                    }}
                    className="px-4 py-2 text-gray-600 hover:text-gray-800 border rounded"
                  >
                    Annuler
                  </button>
                  <button
                    onClick={() => handleAssignCompetencies(selectedUserForCompetencies.id)}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                  >
                    Enregistrer ({selectedCompetencies.length})
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <ChangePasswordModal
        isOpen={isPasswordModalOpen}
        onClose={() => setIsPasswordModalOpen(false)}
        email={currentUserEmail}
      />
    </div>
  );
};

export default TenantAdminDashboard;